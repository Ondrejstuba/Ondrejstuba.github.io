import socket, threading, time, os, sys, random, re, subprocess, platform, ipaddress
import tkinter as tk
from tkinter import scrolledtext, Tk, filedialog, ttk, PhotoImage
from PIL import Image, ImageTk, ImageOps, ImageDraw
import winsound
from datetime import datetime
import psutil
import cv2 
sys.stdout = open(os.devnull, 'w')
import pygame
sys.stdout = sys.__stdout__
import logging


"""
1 file metadata   2 file fragment    3 message fragment    4 heartbeat    5 handshake    6 handshake ack   7 ack/nack/ready/dontwant    8 closed   9 errorchance    10 stop

NAJLEPSIE JE V CMD VYTVORIT EXE, TAKTO:
pyinstaller --onefile --windowed --icon=profilovky/icon.ico --add-data "profilovky;profilovky" --add-data "zvuky;zvuky" --name="UDPChat" UDPChat.pyw

alebo druha moznost je nainstalovat vsetky potrebne libraries, takto:
pip install socket threading time os sys random re subprocess platform ipaddress tkinter pillow winsound psutil opencv-python pygame
"""

BUFFER_SIZE = 1024  # max fragment velkost
FILE_BUFFER_SIZE = 1024  # max fragment velkost file
HEARTBEAT_INTERVAL = 5  # 5 sekund
HEARTBEAT_TIMEOUT = 18
ERROR_CHANCE = 0
TIMEOUT_THRESHOLD_NACK = 5
STOP_PROCESSING_TIMEOUT = 1
MAX_BATCH_SIZE = 200

class P2PNode:
    def __init__(self, listen_port, target_ip, target_port, gui, error_chance, logger=None):
        self.listen_port = listen_port
        self.target_ip = target_ip
        self.target_port = target_port
        self.sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        self.sock.bind(('0.0.0.0', listen_port))
        self.heartbeat_missed = 0
        self.gui = gui  
        self.running = True
        self.handshake_complete = False  
        self.current_message = ""
        self.fragment_in_progress = False 
        self.fragment_number = 1
        self.ack_received = False
        self.lock = threading.Lock()  
        self.last_heartbeat_time = time.time()
        self.fragment_start_time = {}  
        self.peer_disconnected = False 
        self.is_file_transfer_window_open = False  
        self.logger = logger or logging.getLogger(__name__)
        self.total_fragments = 0
        self.everythingreceived = False

        self.total_fragments_received = {} 
        self.erroneous_fragments = {}
        self.ack_count = 0  
        self.nack_count = 0 

    
        self.ARRAY_FOR_FRAG_SEND = {}  # msg (send)
        self.ARRAY_FOR_FRAG_FILE_SEND = {}  # file (send)

        self.ARRAY_FOR_FRAG_REC = {}  # msg  (rec)
        self.ARRAY_FOR_FRAG_FILE_REC = {}  # file (rec)

        self.file_extension = {}

        self.error_chance = error_chance
        if self.error_chance == 0:
            self.send_delay = 0.004
        elif 0 < self.error_chance <= 1:
            self.send_delay = 0.01
        elif 1 < self.error_chance <= 3:
            self.send_delay = 0.04
        elif 3 < self.error_chance:
            self.send_delay = 0.09

        self.logger.normal(f"Peer Error Chance: {self.error_chance}%")
        self.logger.normal(f"Corresponding Send Delay Between Fragments: {self.send_delay} seconds")

    def send_handshake(self):
        handshake_message = "5\x1D\x1EHANDSHAKE"  
        self.sock.sendto(handshake_message.encode(), (self.target_ip, self.target_port))
        self.logger.send(f"Sent handshake to {self.target_ip}:{self.target_port}")
        if self.gui.show_system_messages.get():
            self.safe_display_message(f"Sent handshake to {self.target_ip}:{self.target_port}", sender="System")
        self.gui.update_handshake_progress(1)

    def send_disconnect_notification(self):
        disconnect_message = "8\x1D\x1EDISCONNECT" 
        self.sock.sendto(disconnect_message.encode(), (self.target_ip, self.target_port))
        self.logger.send(f"Sent disconnect notification to {self.target_ip}:{self.target_port}")
        if self.gui.show_system_messages.get():
            self.safe_display_message(f"Sent disconnect notification to {self.target_ip}:{self.target_port}", sender="System")

    def notify_error_chance_update(self, error_chance):
        try:
            notification_message = f"9\x1D\x1E{error_chance}"
            self.sock.sendto(notification_message.encode(), (self.target_ip, self.target_port))

            self.logger.send(f"You changed your error chance to {error_chance}%. Notification sent to peer.")
            if self.gui.show_system_messages.get():
                self.safe_display_message(f"You changed your error chance to {error_chance}%. Notification sent to peer.", sender="System")
        
        except Exception as e:
            self.logger.error(f"Failed to send error chance update notification: {str(e)}")
            if self.gui and self.gui.show_system_messages.get():
                self.safe_display_message(f"Failed to send error chance update notification: {str(e)}", sender="System")

    def calculate_crc16(self, data: bytes) -> int:
        crc = 0xFFFF
        poly = 0xA001 

        for byte in data:
            crc ^= byte
            for _ in range(8):  
                if crc & 0x0001:
                    crc = (crc >> 1) ^ poly
                else:
                    crc >>= 1
        return crc & 0xFFFF  
        
    def send_file(self, file_path):
        if self.handshake_complete:
            with open(file_path, 'rb') as file:
                self.file_data = file.read() 
        
            filename = os.path.basename(file_path)
            file_size = len(self.file_data)
            self.total_fragments = (file_size + FILE_BUFFER_SIZE - 1) // FILE_BUFFER_SIZE  
            metadata_message = f"1\x1D\x1E{filename}\x1D\x1E{file_size}\x1D\x1E{self.total_fragments}"
            self.sock.sendto(metadata_message.encode(), (self.target_ip, self.target_port))
            self.logger.send(f"Waiting for 'READY' signal from receiver...")

    def send_file_fragments(self, file_data):
        self.ack_count = 0
        self.nack_count = 0
        self.ARRAY_FOR_FRAG_FILE_SEND.clear()
        self.fragment_in_progress = True  
        self.gui.update_heartbeat_area_color(True) 
        self.fragment_number = 1
        self.everythingreceived = False
        self.ack_received = False

        use_batches = self.total_fragments > 100
        max_batch_size = MAX_BATCH_SIZE
        batch_size = min(max_batch_size, max(1, self.total_fragments // 10)) if use_batches else 1
        self.logger.send(f"Total fragments: {self.total_fragments}, ERROR_CHANCE: {self.error_chance}")
        if self.gui.show_system_messages.get():
            self.safe_display_message(f"Starting to send file in {self.total_fragments} fragments.", sender="System")

        while self.fragment_number <= self.total_fragments:
            end_of_batch = min(self.fragment_number + batch_size - 1, self.total_fragments)
            fragments_sent_in_batch = []

            for frag_num in range(self.fragment_number, end_of_batch + 1):
                start_index = (frag_num - 1) * FILE_BUFFER_SIZE
                fragment = file_data[start_index:start_index + FILE_BUFFER_SIZE]

                is_last_fragment = (frag_num == self.total_fragments)
                if is_last_fragment and len(fragment) < FILE_BUFFER_SIZE:
                    if self.gui.show_system_messages.get():
                        self.safe_display_message(f"Last fragment is smaller ({len(fragment)} bytes).", sender="System")
                    self.logger.send(f"Last fragment is smaller ({len(fragment)} bytes).")

                # Create the fragment header (including fragment number and total fragments)
                if frag_num == 1:
                    fragment_header = f"2\x1D\x1E{frag_num}/{self.total_fragments}\x1D\x1E".encode()
                else:
                    fragment_header = f"2\x1D\x1E{frag_num}\x1D\x1E".encode()

                # Combine header and data for the fragment
                numbered_fragment = fragment_header + fragment  

                # Calculate CRC and append it
                crc = self.calculate_crc16(numbered_fragment)
                numbered_fragment_with_crc = numbered_fragment + f"\x1D\x1E{crc:04X}".encode()

                # Store the fragment for later use
                self.ARRAY_FOR_FRAG_FILE_SEND[frag_num] = numbered_fragment_with_crc

                # Send the fragment
                self.sock.sendto(numbered_fragment_with_crc, (self.target_ip, self.target_port))
                self.logger.send(f"{frag_num}/{self.total_fragments}")
                fragments_sent_in_batch.append(frag_num)

                # Introduce delay between fragment sends
                time.sleep(self.send_delay)
               
            # Wait for ACKs if using batches
            if use_batches:
                self.logger.send(f"Waiting for ACK for batch {self.fragment_number} to {end_of_batch}.")
                if self.gui.show_system_messages.get():
                    self.safe_display_message(f"Waiting for ACK for batch {self.fragment_number} to {end_of_batch}.", sender="System")

                with self.lock:
                    while not self.ack_received:
                        self.lock.release()
                        self.lock.acquire()

                self.ack_received = False
                self.last_heartbeat_time = time.time()

            # Update the fragment number for the next batch
            self.fragment_number = end_of_batch + 1 

        if self.gui.show_system_messages.get():
            self.safe_display_message(
                f"Successfully sent all {self.total_fragments} file fragments. ", sender="System"
            )

        self.logger.send(f"Successfully sent all {self.total_fragments} fragments.")
        self.logger.send(f"Total ACKs received: {self.ack_count}, Total NACKs received: {self.nack_count}.")
        
    


    def send_message(self, message):
        if self.handshake_complete:
                self.ack_count = 0
                self.nack_count = 0
                self.ARRAY_FOR_FRAG_SEND.clear()
                self.fragment_number = 1
                self.everythingreceived = False
                self.ack_received = False  
                self.total_fragments = (len(message) + BUFFER_SIZE - 1) // BUFFER_SIZE 
                self.fragment_in_progress = True 
                self.gui.update_heartbeat_area_color(True)
                threading.Thread(target=self.send_fragments, args=(message,), daemon=True).start()
        else:
            if self.gui.show_system_messages.get():
                self.safe_display_message("Handshake not completed yet. Cannot send message.", sender="System")

    def send_fragments(self, message):

        if self.gui.show_system_messages.get():
            self.safe_display_message(f"Starting to send message in {self.total_fragments} fragments", sender="System")

        use_batches = self.total_fragments > 100
        max_batch_size = MAX_BATCH_SIZE
        batch_size = min(max_batch_size, max(1, self.total_fragments // 10)) if use_batches else 1
        self.logger.send(f"Total fragments: {self.total_fragments}, ERROR_CHANCE: {self.error_chance}")

        self.logger.send(f"Batching enabled: {use_batches}, Batch size: {batch_size}")

        while self.fragment_number <= self.total_fragments:
            end_of_batch = min(self.fragment_number + batch_size - 1, self.total_fragments)
            fragments_sent_in_batch = []

            for frag_num in range(self.fragment_number, end_of_batch + 1):
                start_index = (frag_num - 1) * BUFFER_SIZE
                fragment = message[start_index:start_index + BUFFER_SIZE]

                if frag_num == 1:
                    first_fragment_size = len(fragment)

                is_last_fragment = (frag_num == self.total_fragments)

                if is_last_fragment and self.total_fragments > 1:
                    if len(fragment) != first_fragment_size:
                        if self.gui.show_system_messages.get():
                            self.safe_display_message(f"Last fragment is smaller ({len(fragment)} bytes) than the previous ({BUFFER_SIZE} bytes).", sender="System")

                if frag_num == 1:
                    numbered_fragment = f"3\x1D\x1E{frag_num}/{self.total_fragments}\x1D\x1E{fragment}"
                else:
                    numbered_fragment = f"3\x1D\x1E{frag_num}\x1D\x1E{fragment}"

                crc = self.calculate_crc16(numbered_fragment.encode())
                numbered_fragment_with_crc = f"{numbered_fragment}\x1D\x1E{crc:04X}"

                self.ARRAY_FOR_FRAG_SEND[frag_num] = numbered_fragment_with_crc

                self.last_heartbeat_time = time.time()

                fragment_size = len(fragment)

                if self.gui.show_sent_fragments.get():
                    fragment_display = f"{frag_num}/{self.total_fragments}: {fragment} (CRC: {crc:04X}, Size: {fragment_size})"
                    self.safe_display_message(fragment_display, sender="You")

                self.sock.sendto(numbered_fragment_with_crc.encode(), (self.target_ip, self.target_port))
                self.logger.send(f"{frag_num}/{self.total_fragments}")
                fragments_sent_in_batch.append(frag_num)

                if use_batches:
                    time.sleep(self.send_delay)  

            if use_batches:
                self.logger.send(f"Waiting for ACK for batch {self.fragment_number} to {end_of_batch}.")

                if self.gui.show_system_messages.get():
                    self.safe_display_message(f"Waiting for ACK for batch {self.fragment_number} to {end_of_batch}.", sender="System")

                with self.lock:
                    while not self.ack_received:
                        self.lock.release()
                        self.lock.acquire()

                self.ack_received = False
                self.last_heartbeat_time = time.time()

            self.fragment_number = end_of_batch + 1

        if self.gui.show_system_messages.get():
            self.safe_display_message(f"Successfully sent all {self.total_fragments} fragments.", sender="System")
        self.logger.send(f"Successfully sent all {self.total_fragments} fragments.")
        self.logger.send(f"Total ACKs received: {self.ack_count}, Total NACKs received: {self.nack_count}.")

    def handle_ack_nack(self, ack_message, addr):
                self.last_heartbeat_time = time.time()

                if ack_message.startswith("ACK"):
                    self.ack_count += 1 
                    if " " in ack_message:
                        ack_range = ack_message.split()[1]
                        start, end = map(int, ack_range.split("-"))
                        if start == 1 and end == self.total_fragments: 
                            if self.gui.show_system_messages.get():
                                self.safe_display_message(f"Sender received ACK for all fragments.", sender="System")
                            self.logger.send(f"ACK received for all fragments.")
                            self.fragment_in_progress = False
                            self.gui.update_heartbeat_area_color(False)
                            self.everythingreceived = True
                            stop_message = "10\x1D\x1ESTOP"  
                            self.sock.sendto(stop_message.encode(), (self.target_ip, self.target_port))
                            return 

                    if self.gui.show_system_messages.get():
                        self.safe_display_message(f"Sender received ACK for fragments {ack_message}", sender="System")
                    self.logger.send(f"ACK received for fragments {ack_message}.")

                    with self.lock:
                        self.ack_received = True
                    return
                
                elif ack_message.startswith("NACK"):
                    if self.everythingreceived:
                        self.logger.send(f"This NACK is meant to be skipped...")
                        return
                    nack_numbers = [int(num) for num in ack_message.split()[1:]]  
                    self.nack_count += 1  

                    for nack_num in nack_numbers:
                        if nack_num in self.ARRAY_FOR_FRAG_SEND: 
                            fragment_to_resend = self.ARRAY_FOR_FRAG_SEND[nack_num]

                            if self.gui.show_system_messages.get():
                                self.safe_display_message(f"NACK received for fragment {nack_num}. Resending.", sender="System")
                            self.logger.send(f"NACK received for file fragment {nack_num}. Resending fragment.")

                            with self.lock:
                                self.sock.sendto(fragment_to_resend.encode(), (self.target_ip, self.target_port))
                                self.lock.notify()
                                self.logger.send(f"Resent fragment {fragment_to_resend}.")

                        elif nack_num in self.ARRAY_FOR_FRAG_FILE_SEND:
                            fragment_to_resend = self.ARRAY_FOR_FRAG_FILE_SEND[nack_num]
                            if self.gui.show_system_messages.get():
                                self.safe_display_message(f"NACK received for file fragment {nack_num}. Resending.", sender="System")
                            self.logger.send(f"NACK received for fragment {nack_num}. Resending fragment.")

                            with self.lock:
                                self.sock.sendto(fragment_to_resend, (self.target_ip, self.target_port))
                                self.lock.notify()
                                self.logger.send(f"Resent fragment {fragment_to_resend}.")

                elif ack_message.startswith("READY"):
                    if self.gui.show_system_messages.get():
                        self.safe_display_message("Sender is ready to start sending fragments.", sender="System")
                    self.logger.send(f"Sender is ready to start sending fragments.")

                    threading.Thread(target=self.send_file_fragments, args=(self.file_data,), daemon=True).start()
                    return

                elif ack_message.startswith("DONTWANT"):
                    if self.gui.show_system_messages.get():
                        self.safe_display_message("Receiver does not want to receive the file. Cancelling transfer.", sender="System")
                    self.logger.send(f"Receiver does not want to receive the file. Cancelling transfer.")

                    self.ARRAY_FOR_FRAG_FILE_SEND.clear()
                    self.fragment_in_progress = False
                    self.gui.update_heartbeat_area_color(False)
                    self.fragment_number = 1
                    self.is_file_transfer_window_open = False
                    return

    def receive_message(self):

        while self.running:
            try:    

                data, addr = self.sock.recvfrom(1400) 
                self.last_heartbeat_time = time.time()
                self.gui.reset_elapsed_time() 

                if self.fragment_in_progress and (not hasattr(self, "file_timeout_checker_thread") or not self.file_timeout_checker_thread.is_alive()) and time.time() - self.last_heartbeat_time > TIMEOUT_THRESHOLD_NACK:
                    self.file_timeout_checker_thread = threading.Thread(target=self.check_for_missing_file_fragments, args=(addr,), daemon=True)
                    self.file_timeout_checker_thread.start()

                if not hasattr(self, "timeout_checker_thread") or not self.timeout_checker_thread.is_alive():
                            if time.time() - self.last_heartbeat_time > TIMEOUT_THRESHOLD_NACK:
                                self.timeout_checker_thread = threading.Thread(target=self.check_for_missing_fragments, args=(addr,), daemon=True)
                                self.timeout_checker_thread.start()

                if data.startswith(b"10"):
                    self.logger.receive("Received STOP_PROCESSING message. Ignoring further messages temporarily.")
                    if self.gui.show_system_messages.get():
                        self.safe_display_message("Ignoring further data temporarily.", sender="System")
                    self.fragment_in_progress = False
                    time.sleep(STOP_PROCESSING_TIMEOUT)  
                    stop_processing = False  
                    if self.ARRAY_FOR_FRAG_REC:
                        del self.ARRAY_FOR_FRAG_REC[addr]
                    if self.fragment_start_time:
                        del self.fragment_start_time[addr]
                    if self.total_fragments_received:
                        del self.total_fragments_received[addr]
                    if self.erroneous_fragments:
                        del self.erroneous_fragments[addr]
                    self.logger.receive(f"Resuming data processing after timeout of {STOP_PROCESSING_TIMEOUT} seconds.")
                    if self.gui.show_system_messages.get():
                        self.safe_display_message("Resuming data processing.", sender="System")
                    continue

                if data.startswith(b"2"):

                        
                        parts = data.split(b"\x1D\x1E") 
                        
                        fragment_info = parts[1]  # "1/16"
                        received_crc_hex = parts[-1]  # CRC
                        fragment_data = b"\x1D\x1E".join(parts[2:-1])  # Data

                        if addr not in self.fragment_start_time:
                            self.fragment_start_time[addr] = time.time()
                            self.erroneous_fragments[addr] = 0 
                            self.total_fragments_received[addr] = 0  

                        if b"/" in fragment_info:
                            fragment_number_rec, total_fragments = map(int, fragment_info.decode('utf-8', 'ignore').split("/"))

                            self.fragment_in_progress = True 
                            self.gui.update_heartbeat_area_color(True)

                            if addr not in self.ARRAY_FOR_FRAG_FILE_REC:
                                self.ARRAY_FOR_FRAG_FILE_REC[addr] = [None] * total_fragments
                        else:
                            fragment_number_rec = int(fragment_info.decode('utf-8', 'ignore'))   

                        if random.random() < ERROR_CHANCE / 100:
                            if len(fragment_data) > 0:
                                error_index = random.randint(0, len(fragment_data) - 1)
                                error_simulated_fragment_data = bytearray(fragment_data)
                                error_simulated_fragment_data[error_index] = (error_simulated_fragment_data[error_index] + 1) % 256
                                fragment_data = bytes(error_simulated_fragment_data) 

                        received_crc = int(received_crc_hex.decode('utf-8', 'ignore'), 16)

                        numbered_fragment = b"2\x1D\x1E" + fragment_info + b"\x1D\x1E" + fragment_data 

                        calculated_crc = self.calculate_crc16(numbered_fragment)    

                        self.total_fragments_received[addr] += 1
               
                        if calculated_crc != received_crc:
                            self.logger.receive(f"CRC mismatch on fragment {fragment_number_rec}/{total_fragments}. Expected CRC: {received_crc:04X}, Calculated CRC: {calculated_crc:04X}. Sending NACK.")
                            self.erroneous_fragments[addr] += 1

                            if self.gui.show_system_messages.get():
                                self.safe_display_message(f"CRC mismatch on fragment {fragment_number_rec}/{total_fragments}. Sending NACK.", sender="System")
                            threading.Thread(target=self.send_ack_nack, args=(addr, f"NACK {fragment_number_rec}"), daemon=True).start()
                            continue

                        self.logger.receive(f"Saved {fragment_number_rec} at {fragment_number_rec - 1}")

                        self.ARRAY_FOR_FRAG_FILE_REC[addr][fragment_number_rec - 1] = fragment_data  # do daneho slotu

                        if self.ARRAY_FOR_FRAG_FILE_REC[addr][fragment_number_rec - 1] is None:
                            self.logger.error(f"Failed to store fragment {fragment_number_rec} in slot {fragment_number_rec - 1} for address {addr}. The slot is still None.")

                        if total_fragments > 10:
                            max_batch_size = MAX_BATCH_SIZE
                            batch_size = min(max_batch_size, max(1, total_fragments // 10))

                            batch_index = (fragment_number_rec - 1) // batch_size  
                            ack_range_start = batch_index * batch_size + 1
                            ack_range_end = min((batch_index + 1) * batch_size, total_fragments)

                            batch_received = False 

                            last_checked_index = fragment_number_rec - 1

                            batch_received = all(
                                self.ARRAY_FOR_FRAG_FILE_REC[addr][i]
                                for i in range(ack_range_start - 1, ack_range_end)
                            )

                            if batch_received:
                                ack_message = f"ACK {ack_range_start}-{ack_range_end}"
                                threading.Thread(target=self.send_ack_nack, args=(addr, ack_message), daemon=True).start()
                                self.logger.receive(f"Sent ACK for batch {ack_range_start}-{ack_range_end}.")
                                if self.gui.show_system_messages.get():
                                    self.safe_display_message(f"Sent ACK for batch {ack_range_start}-{ack_range_end}.", sender="System")
                                
                            else:
                                if fragment_number_rec == ack_range_end:
                                    missing_fragments = [
                                        i + 1
                                        for i in range(ack_range_start - 1, last_checked_index + 1)
                                        if not self.ARRAY_FOR_FRAG_FILE_REC[addr][i]
                                    ]

                                    if missing_fragments:
                                        missing_fragment = missing_fragments[0]
                                        self.logger.receive(f"Missing fragment {missing_fragment}. Sending NACK.")
                                        threading.Thread(
                                            target=self.send_ack_nack,
                                            args=(addr, f"NACK {missing_fragment}"),
                                            daemon=True
                                        ).start()

                        if {fragment_number_rec} == total_fragments:
                                missing_fragments = [i + 1 for i, fragment in enumerate(self.ARRAY_FOR_FRAG_FILE_REC[addr]) if fragment is None]
                                if self.gui.show_system_messages.get():
                                    self.safe_display_message(f"Missing fragments: {', '.join(map(str, missing_fragments))}. Sending NACKs.", sender="System")
                                if missing_fragments:
                                    for missing_fragment in missing_fragments:
                                        self.logger.receive(f"Missing fragment {missing_fragment}. Sending NACK.")
                                        threading.Thread(target=self.send_ack_nack, args=(addr, f"NACK {missing_fragment}"), daemon=True).start()
                     
                        if all(fragment is not None for fragment in self.ARRAY_FOR_FRAG_FILE_REC[addr]): 
                            self.logger.receive(f"All fragments received for {addr}. Total fragments: {total_fragments}. "
                                                f"Erroneous fragments: {self.erroneous_fragments[addr]}."
                                                f"All fragments in traffic: {self.total_fragments_received[addr]}.")

                            start_time = self.fragment_start_time[addr]
                            end_time = time.time()
                            elapsed_time = end_time - start_time 

                            if self.gui.show_system_messages.get():
                                
                                self.safe_display_message(f"Successfully received and saved file at {save_location}.", sender="System", total_fragments=total_fragments, elapsed_time=elapsed_time)
                                self.gui.play_sound()


                            with open(save_location, 'wb') as file: 
                                for fragment in self.ARRAY_FOR_FRAG_FILE_REC[addr]:
                                    file.write(fragment)
                            self.logger.receive(f"File saved at {save_location}.")

                            if file_extension in [".jpg", ".jpeg", ".png", ".gif", ".bmp"]:
                                try:
                                    image = Image.open(save_location)
                                    image.verify()

                                    self.gui.display_message("Here is an image:", sender=addr[0], total_fragments=total_fragments, elapsed_time=elapsed_time, image_file=save_location)

                                except (IOError, SyntaxError) as e:
                                    self.logger.error(f"The received file is not a valid image: {e}")

                            elif file_extension in [".mp3", ".wav", ".ogg"]:                              
                                self.gui.display_message("Here is an audio:", sender=addr[0], total_fragments=total_fragments, elapsed_time=elapsed_time, audio_file=save_location)

                            elif file_extension in [".pdf", ".docx", ".doc", ".txt", ".xlsx", ".pptx", ".odt", ".rtf", ".csv"]:
                                self.gui.display_message("Here is a document:", sender=addr[0], total_fragments=total_fragments, elapsed_time=elapsed_time, doc_file=save_location)

                            elif file_extension in [".mp4", ".avi", ".mov", ".mkv", ".flv", ".wmv"]:
                                 self.gui.display_message("Here is a video:", sender=addr[0], total_fragments=total_fragments, elapsed_time=elapsed_time, video_file=save_location)

                            final_ack_message = f"ACK 1-{total_fragments}"
                            threading.Thread(target=self.send_ack_nack, args=(addr, final_ack_message), daemon=True).start()
                            self.logger.receive(f"Sent final ACK for all fragments: {final_ack_message}")
               
                            self.fragment_in_progress = False
                            self.gui.update_heartbeat_area_color(False)
                            del self.ARRAY_FOR_FRAG_FILE_REC[addr]      
                            del self.fragment_start_time[addr] 
                            del self.file_extension[addr]  
                            del self.total_fragments_received[addr]
                            del self.erroneous_fragments[addr]

                message = data.decode(errors='ignore')
                
                if message.startswith("7\x1D\x1E"):
                    ack_nack_message = message[3:]

                    if self.gui.show_ack_nack.get():
                        ack_nack_display = f"{ack_nack_message}"
                        self.safe_display_message(ack_nack_display, sender=addr[0])
                        
                    self.handle_ack_nack(ack_nack_message, addr)
                        
                if message.startswith("9\x1D\x1E"):
                    try:
                        error_chance = message[3:] 

                        self.error_chance = float(error_chance)

                        if self.error_chance == 0:
                            self.send_delay = 0.004
                        elif 0 < self.error_chance <= 1:
                            self.send_delay = 0.009
                        elif 1 < self.error_chance <= 3:
                            self.send_delay = 0.01
                        elif 3 < self.error_chance:
                            self.send_delay = 0.09

                        if self.gui.show_system_messages.get():
                            self.safe_display_message(f"Received peer error chance update: {self.error_chance}%. Adjusted sleep duration to {self.send_delay:.3f} seconds.", sender="System")
                        
                    except ValueError as e:
                        if self.gui.show_system_messages.get():
                            self.safe_display_message(f"Invalid error chance received: {str(e)}", sender="System")
                    continue                   

                if message.startswith("5\x1D\x1EHANDSHAKE"):
                    if self.peer_disconnected:
                        if self.gui.show_system_messages.get():
                            self.safe_display_message(f"Reconnection attempt detected from {addr[0]}. Initiating handshake again.", sender="System")
                        self.handshake_complete = True 
                        self.sock.sendto(b"6\x1D\x1EHANDSHAKE_ACK", addr)
                        self.gui.update_handshake_progress(2)  # 2/3
                    else:
                        self.handshake_complete = True
                        if self.gui.show_system_messages.get():
                            self.safe_display_message(f"Handshake received from {addr[0]}. Sending response.", sender="System")
                        self.sock.sendto(b"6\x1D\x1EHANDSHAKE_ACK", addr)
                        self.gui.update_handshake_progress(2)  # 2/3
                elif message.startswith("6\x1D\x1EHANDSHAKE_ACK"):
                    self.handshake_complete = True
                    if self.gui.show_system_messages.get():
                        self.safe_display_message(f"Handshake acknowledged by {addr[0]}. Connection established.", sender="System")
                    self.gui.update_handshake_progress(3)  # 3/3
                elif message.startswith("4\x1D\x1EHEARTBEAT"):
                     if self.gui.show_received_heartbeat.get(): 
                         self.safe_display_heartbeat(f" {addr}", message_type="received")
                     self.gui.reset_elapsed_time() 
                     self.gui.update_handshake_progress(3)
                     self.last_heartbeat_time = time.time()
                elif message.startswith("8\x1D\x1EDISCONNECT"):
                    if self.gui.show_system_messages.get():
                        self.safe_display_message(f"Peer at {addr[0]} has disconnected.", sender="System")
                    self.peer_disconnected = True        
                    self.handshake_complete = True  
                    self.gui.update_handshake_progress(0)
                    continue
                
                elif message.startswith("1\x1D\x1E"):
                    _, filename, file_size, total_fragments = message.split("\x1D\x1E")
                    total_fragments = int(total_fragments)
                    file_size = int(file_size)

                    base_filename, file_extension = os.path.splitext(filename)
                    self.file_extension = file_extension.lower()  

                    self.is_file_transfer_window_open = True

                    root = Tk()
                    root.withdraw() 

                    try:
                        save_location = filedialog.asksaveasfilename(initialfile=filename, title="Save File OR CANCEL RECEIVE")
                    except Exception as e:
                        self.logger.error(f"Error while opening save dialog: {e}")
                        threading.Thread(target=self.send_ack_nack, args=(addr, "DONTWANT"), daemon=True).start()
                        self.is_file_transfer_window_open = True
                        if self.gui.show_system_messages.get():
                            self.safe_display_message(f"Error while opening save dialog: {e}", sender="System")
                        return

                    root.destroy()

                    if save_location:
                        threading.Thread(target=self.send_ack_nack, args=(addr, "READY"), daemon=True).start()

                        if self.gui.show_system_messages.get():
                            self.safe_display_message(f"Receiving file {filename} ({file_size} bytes) in {total_fragments} fragments.", sender="System")
                    
                    else:
                        if self.gui.show_system_messages.get():
                            self.safe_display_message(f"You cancelled reception of {filename} ({file_size} bytes) in {total_fragments} fragments.", sender="System")
                        threading.Thread(target=self.send_ack_nack, args=(addr, "DONTWANT"), daemon=True).start()

                    self.is_file_transfer_window_open = False
                else:

                    if message.startswith("3\x1D\x1E"):

                        self.last_heartbeat_time = time.time()

                        if addr not in self.fragment_start_time:
                            self.fragment_start_time[addr] = time.time()
                            self.total_fragments_received[addr] = 0  
                            self.erroneous_fragments[addr] = 0  
                        
                        message_content = message[2:] 

                        fragment_info, fragment_data_crc = message_content.split("\x1D\x1E", 1)
                        fragment_info = fragment_info.strip()

                        fragment_data, received_crc_hex = fragment_data_crc.rsplit("\x1D\x1E", 1)
                        received_crc = int(received_crc_hex, 16)

                        if random.random() < ERROR_CHANCE / 100:
                            if len(fragment_data) > 0:
                                error_simulated_fragment_data = 'X' + fragment_data[1:] 
                            else:
                                error_simulated_fragment_data = fragment_data
                        else:
                            error_simulated_fragment_data = fragment_data

                        calculated_crc = self.calculate_crc16(f"3\x1D\x1E{fragment_info}\x1D\x1E{error_simulated_fragment_data}".encode())

                        if "/" in fragment_info:
                            fragment_number_rec, total_fragments = map(int, fragment_info.split("/"))

                            self.fragment_in_progress = True
                            self.gui.update_heartbeat_area_color(True)

                            if addr not in self.ARRAY_FOR_FRAG_REC:
                                self.ARRAY_FOR_FRAG_REC[addr] = [""] * total_fragments
                        else:
                            fragment_number_rec = int(fragment_info)

                        self.total_fragments_received[addr] += 1

                        if calculated_crc != received_crc:
                            self.logger.receive(f"CRC mismatch on fragment {fragment_number_rec}/{total_fragments}. Sending NACK.")
                            self.erroneous_fragments[addr] += 1
                            if self.gui.show_system_messages.get():
                                self.safe_display_message(f"CRC mismatch on fragment {fragment_number_rec}/{total_fragments}. Sending NACK.", sender="System")
                            threading.Thread(target=self.send_ack_nack, args=(addr, f"NACK {fragment_number_rec}"), daemon=True).start()
                            continue  
                        
                        if self.gui.show_sent_fragments.get():
                            fragment_display = f"{fragment_number_rec}/{total_fragments}: {fragment_data} (CRC: {calculated_crc:04X})"
                            self.safe_display_message(fragment_display, sender=addr[0])

                        self.ARRAY_FOR_FRAG_REC[addr][fragment_number_rec - 1] = fragment_data

                        self.logger.receive(f"Saved {fragment_number_rec} at {fragment_number_rec - 1}")

                        if total_fragments > 10:
                            max_batch_size = MAX_BATCH_SIZE
                            batch_size = min(max_batch_size, max(1, total_fragments // 10))

                            batch_index = (fragment_number_rec - 1) // batch_size  
                            ack_range_start = batch_index * batch_size + 1
                            ack_range_end = min((batch_index + 1) * batch_size, total_fragments)

                            batch_received = False 

                            last_checked_index = fragment_number_rec - 1

                            batch_received = all(
                                self.ARRAY_FOR_FRAG_REC[addr][i]
                                for i in range(ack_range_start - 1, ack_range_end)
                            )

                            if batch_received:
                                ack_message = f"ACK {ack_range_start}-{ack_range_end}"
                                threading.Thread(target=self.send_ack_nack, args=(addr, ack_message), daemon=True).start()
                                self.logger.receive(f"Sent ACK for batch {ack_range_start}-{ack_range_end}.")
                                if self.gui.show_system_messages.get():
                                    self.safe_display_message(f"Sent ACK for batch {ack_range_start}-{ack_range_end}.", sender="System")
                                

                            else:
                                if fragment_number_rec == ack_range_end:
                                    missing_fragments = [
                                        i + 1
                                        for i in range(ack_range_start - 1, last_checked_index + 1)
                                        if not self.ARRAY_FOR_FRAG_REC[addr][i]
                                    ]

                                    if missing_fragments:
                                        missing_fragment = missing_fragments[0]
                                        self.logger.receive(f"Missing fragment {missing_fragment}. Sending NACK.")
                                        if self.gui.show_system_messages.get():
                                            self.safe_display_message(f"Missing fragment {missing_fragment}. Sending NACK.", sender="System")
                                        threading.Thread(
                                            target=self.send_ack_nack,
                                            args=(addr, f"NACK {missing_fragment}"),
                                            daemon=True
                                        ).start()


                        if fragment_number_rec == total_fragments:
                           
                            missing_fragments = [i + 1 for i, received in enumerate(self.ARRAY_FOR_FRAG_REC[addr]) if not received]
                            if missing_fragments:
                                if self.gui.show_system_messages.get():
                                    self.safe_display_message(f"Missing fragments: {', '.join(map(str, missing_fragments))}. Sending NACKs.", sender="System")
                                for missing_fragment in missing_fragments:
                                    self.logger.receive(f"Missing fragment {missing_fragment}. Sending NACK.")
                                    threading.Thread(target=self.send_ack_nack, args=(addr, f"NACK {missing_fragment}"), daemon=True).start()

                        if all(self.ARRAY_FOR_FRAG_REC[addr]):
                            full_message = "".join(self.ARRAY_FOR_FRAG_REC[addr])
                            self.logger.receive(f"All fragments received for {addr}. Total good fragments: {total_fragments}. "
                                                f"Erroneous fragments: {self.erroneous_fragments[addr]}." 
                                                f"All fragments in traffic: {self.total_fragments_received[addr]}.")
        

                            start_time = self.fragment_start_time[addr]
                            end_time = time.time()
                            elapsed_time = end_time - start_time 
                            
                            self.safe_display_message(full_message, sender=addr[0], total_fragments=total_fragments, elapsed_time=elapsed_time)
                            
                            self.gui.play_sound()
                            if self.gui.show_system_messages.get():
                                self.safe_display_message(f"Successfully received all {total_fragments} fragments.", sender="System")

                            final_ack_message = f"ACK 1-{total_fragments}"
                            threading.Thread(target=self.send_ack_nack, args=(addr, final_ack_message), daemon=True).start()
                            self.logger.receive(f"Sent final ACK for all fragments: {final_ack_message}")

                            self.fragment_in_progress = False
                            self.gui.update_heartbeat_area_color(False)
                            del self.ARRAY_FOR_FRAG_REC[addr]
                            del self.fragment_start_time[addr]
                            del self.total_fragments_received[addr]
                            del self.erroneous_fragments[addr]
                          
            except socket.error as e:
                if e.errno == 10054 or e.errno == 10038:  
                    self.logger.error(f"Socket error: {e}")
                    pass  
                else:
                    self.logger.error(f"Socket error: {e}")
                    if self.gui.show_system_messages.get():
                        self.safe_display_message(f"Something is wrong with the sockets.", sender="System")
                        

            except Exception as e:

                if "'_thread.lock' object has no attribute 'notify'" in str(e):
                    pass

                if isinstance(e, UnicodeDecodeError):
                    pass 
   
    def send_ack_nack(self, addr, message):
        formatted_message = f"7\x1D\x1E{message}"  
        self.sock.sendto(formatted_message.encode(), addr)

    def check_for_missing_fragments(self, addr):
        while self.fragment_in_progress:
            if time.time() - self.last_heartbeat_time > TIMEOUT_THRESHOLD_NACK:
                if not self.fragment_in_progress:  
                    break
                
                missing_fragments = [i + 1 for i, received in enumerate(self.ARRAY_FOR_FRAG[addr]) if not received]

                if missing_fragments:
                    batch_size = 5
                    missing_batches = [missing_fragments[i:i + batch_size] for i in range(0, len(missing_fragments), batch_size)]

                    for batch in missing_batches:
                        self.logger.timeout(f"Missing fragments {batch}. Sending NACKs.")
                        for missing_fragment in batch:
                            threading.Thread(target=self.send_ack_nack, args=(addr, f"NACK {missing_fragment}"), daemon=True).start()
                        time.sleep(0.1)
                
                self.last_heartbeat_time = time.time()


    def check_for_missing_file_fragments(self, addr):
        while self.fragment_in_progress:
            if time.time() - self.last_heartbeat_time > TIMEOUT_THRESHOLD_NACK:
                if not self.fragment_in_progress:  
                    break
                missing_fragments = [i + 1 for i, fragment in enumerate(self.ARRAY_FOR_FRAG_FILE[addr]) if fragment is None]
                
                if missing_fragments:
                    batch_size = 5
                    missing_batches = [missing_fragments[i:i + batch_size] for i in range(0, len(missing_fragments), batch_size)]

                    for batch in missing_batches:
                        self.logger.timeout(f"Missing fragments {batch}. Sending NACKs.")
                        for missing_fragment in batch:
                            threading.Thread(target=self.send_ack_nack, args=(addr, f"NACK {missing_fragment}"), daemon=True).start()
                        time.sleep(0.1) 
        
                self.last_heartbeat_time = time.time()

    def start_heartbeat(self):
        while self.running:
            try:
                if not self.fragment_in_progress:
                    self.sock.sendto(b"4\x1D\x1EHEARTBEAT", (self.target_ip, self.target_port))
                    if self.gui.show_sent_heartbeat.get(): 
                        self.safe_display_heartbeat(f" {self.target_ip}:{self.target_port}", message_type="sent")
                time.sleep(HEARTBEAT_INTERVAL)
            except Exception as e:
                if self.gui.show_system_messages.get():
                    self.safe_display_message(f"Error sending heartbeat: {e}", sender="System")

    def check_heartbeat_timeout(self):
        while self.running:
            current_time = time.time()


            if not self.is_file_transfer_window_open:

                time_since_last_heartbeat = current_time - self.last_heartbeat_time

                if time_since_last_heartbeat > HEARTBEAT_TIMEOUT:
                    if self.gui.show_system_messages.get():
                        self.safe_display_message("Heartbeat timeout exceeded. Disconnecting...", sender="System")
                    self.gui.toggle_connection_by_timeout()  
                    break  

            time.sleep(1) 


    def safe_display_message(self, message, sender="System", total_fragments=None, elapsed_time=None):
        self.gui.root.after(0, self.gui.display_message, message, sender, total_fragments, elapsed_time)

    def safe_display_heartbeat(self, message, message_type="sent"):
        self.gui.root.after(0, self.gui.display_heartbeat, message, message_type)

    def run(self):
        threading.Thread(target=self.receive_message, daemon=True).start()    
        threading.Thread(target=self.check_heartbeat_timeout, daemon=True).start() 
        self.send_handshake() 
        threading.Thread(target=self.start_heartbeat, daemon=True).start()

  
    def stop(self):
        """ Stop the node communication """
        self.running = False
        self.sock.close()

class P2PGUI:
    def __init__(self, root):
        self.root = root
        self.root.title("UDPChat")
        self.root.geometry("760x700")  
        self.root.minsize(600, 600)  
        self.root.configure(bg="#e9ecef") 
        self.start_fade_in_animation()
        self.show_sent_heartbeat = tk.BooleanVar(value=True)
        self.show_received_heartbeat = tk.BooleanVar(value=True) 
        self.theme_var = tk.StringVar(value="light") 
        self.show_ip_address = tk.BooleanVar(value=False)  
        self.show_sent_fragments = tk.BooleanVar(value=False)  
        self.show_system_messages = tk.BooleanVar(value=True)  
        self.selected_sound_var = tk.StringVar(value='none')  
        self.show_ack_nack = tk.BooleanVar(value=False)  
        self.selected_font_size = tk.StringVar()
        self.selected_font_size.set("Normal")  
        self.selected_font_family = tk.StringVar(value="Arial")  
        self.base_path = getattr(sys, '_MEIPASS', os.path.dirname(__file__))
        self.preload_images()
        self.elapsed_time = 0  
        self.update_elapsed_time = False 
        self.show_desktop_notification = tk.BooleanVar(value=True)  
        self.show_total_fragments = tk.BooleanVar(value=True)  
        self.show_elapsed_time = tk.BooleanVar(value=True)
        self.notification_label = None
        self.is_playing = False  
        self.audio_file = None  
        self.is_playing_video = False
        self.video_thread = None
        self.video_label = None
        self.video_file = None
        self.paused = False
        self.cap = None  
        self.set_taskbar_icon()  
        self.show_splash_screen()
        self.node = None
        self.tooltip = None
        self.root.protocol("WM_DELETE_WINDOW", self.on_close)
        self.heartbeat_area_default_bg = "#ffffff"  
        self.heartbeat_area_fragment_bg = "#ffcccc" 

        NORMAL_LEVEL = 25  
        SEND_LEVEL = 21
        RECEIVE_LEVEL = 22
        TIMEOUT_LEVEL = 23
        ERROR_LEVEL = 40  

        logging.addLevelName(NORMAL_LEVEL, "NORMAL")
        logging.addLevelName(SEND_LEVEL, "SEND")
        logging.addLevelName(RECEIVE_LEVEL, "RECEIVE")
        logging.addLevelName(TIMEOUT_LEVEL, "TIMEOUT")
        logging.addLevelName(ERROR_LEVEL, "ERROR")  

        def normal(self, message, *args, **kwargs):
            if self.isEnabledFor(NORMAL_LEVEL):
                self._log(NORMAL_LEVEL, message, args, **kwargs)

        def send(self, message, *args, **kwargs):
            if self.isEnabledFor(SEND_LEVEL):
                self._log(SEND_LEVEL, message, args, **kwargs)

        def receive(self, message, *args, **kwargs):
            if self.isEnabledFor(RECEIVE_LEVEL):
                self._log(RECEIVE_LEVEL, message, args, **kwargs)

        def timeout(self, message, *args, **kwargs):
            if self.isEnabledFor(TIMEOUT_LEVEL):
                self._log(TIMEOUT_LEVEL, message, args, **kwargs)

        def error(self, message, *args, **kwargs):
            if self.isEnabledFor(ERROR_LEVEL):
                self._log(ERROR_LEVEL, message, args, **kwargs)

        logging.Logger.normal = normal
        logging.Logger.send = send
        logging.Logger.receive = receive
        logging.Logger.timeout = timeout
        logging.Logger.error = error

        self.log_file_path = "Developer_stats.txt"  

        if os.path.exists(self.log_file_path):
            try:
                os.remove(self.log_file_path)
            except Exception as e:
                pass

        file_handler = logging.FileHandler(self.log_file_path, mode='a', encoding='utf-8')
        file_handler.setFormatter(logging.Formatter('%(asctime)s - %(levelname)s - %(message)s'))
        file_handler.setLevel(SEND_LEVEL) 

        self.logger = logging.getLogger(__name__)
        self.logger.setLevel(SEND_LEVEL)  
        self.logger.addHandler(file_handler)


        self.logger.normal("UDPChat has been opened.")

        self.logo_photo = None
        logo_path = os.path.join(os.path.dirname(__file__), 'profilovky', 'icon.png')  
        try:
            logo_img = Image.open(logo_path)
            logo_img = logo_img.resize((30, 30))  
            self.logo_photo = ImageTk.PhotoImage(logo_img)
        except Exception as e:
            self.logger.error(f"Error preloading logo: {e}")
            self.logo_photo = None  

        main_frame = tk.Frame(self.root, bg="#e9ecef")
        main_frame.pack(padx=20, pady=10, fill=tk.BOTH, expand=True)

        self.heartbeat_frame = tk.Frame(main_frame, bg="#ffffff", width=250, bd=2, relief="solid")
        self.heartbeat_frame.pack(side=tk.RIGHT, fill=tk.Y, padx=10, pady=10)
        self.heartbeat_frame.pack_propagate(False)

        self.heartbeat_title_frame = tk.Frame(self.heartbeat_frame, bg="#4caf50")  
        self.heartbeat_title_frame.pack(fill=tk.X)

        heartbeat_title = tk.Label(self.heartbeat_title_frame, text="Heartbeats", font=("Arial", 14, "bold"), bg="#4caf50", fg="white", anchor="w", padx=10)
        heartbeat_title.pack(side=tk.LEFT)

        self.heartbeat_interval_label = tk.Label(self.heartbeat_title_frame, text="", font=("Arial", 10), bg="#4caf50", fg="white", anchor="w", padx=10)
        self.heartbeat_interval_label.pack(side=tk.LEFT)

        self.heartbeat_area = scrolledtext.ScrolledText(self.heartbeat_frame, wrap=tk.WORD, state='disabled', height=15, bg="#ffffff", font=("Arial", 10), bd=1, relief="solid")
        self.heartbeat_area.pack(padx=10, pady=(5, 10), fill=tk.BOTH, expand=True)

        self.chat_frame = tk.Frame(main_frame, bg="#ffffff", bd=2, relief="solid")
        self.chat_frame.pack(side=tk.LEFT, fill=tk.BOTH, expand=True, padx=10, pady=10)

        header_frame = tk.Frame(self.chat_frame, bg="#2196f3")
        header_frame.pack(fill=tk.X)

        chat_title = tk.Label(header_frame, text="Chat", font=("Arial", 14, "bold"), bg="#2196f3", fg="white", anchor="w", padx=10)
        chat_title.pack(side=tk.LEFT) 

        self.fullscreen_button = tk.Button(
            header_frame, 
            text="🗖", 
            font=("Arial", 12), 
            bg="#4caf50", 
            fg="white", 
            bd=0, 
            command=self.toggle_fullscreen
        )
        self.fullscreen_button.pack(side=tk.RIGHT, padx=10, pady=5)  

        self.root.bind('<f>', self.toggle_fullscreen)

        self.chat_area = scrolledtext.ScrolledText(self.chat_frame, wrap=tk.WORD, state='disabled', height=15, bg="#f8f9fa", font=("Arial", 11), bd=1, relief="solid")
        self.chat_area.pack(padx=10, pady=(5, 10), fill=tk.BOTH, expand=True)

        message_frame = tk.Frame(self.root, bg="#e9ecef")
        message_frame.pack(padx=20, pady=10, fill=tk.X)

        self.send_file_button = tk.Button(message_frame, text="📁", font=("Arial", 10), bg="#4caf50", fg="white", bd=0, padx=10, pady=5, relief="ridge", activebackground="#45a049", command=self.send_file)
        self.send_file_button.pack(side=tk.LEFT, padx=(0, 10))
        
        self.message_entry = tk.Entry(message_frame, font=("Arial", 14), bg="#ffffff", bd=4, relief="groove")
        self.message_entry.pack(side=tk.LEFT, fill=tk.X, expand=True)

        self.message_entry.insert(0, "Type your message here...")
        self.message_entry.config(fg="grey")

        self.message_entry.bind("<FocusIn>", lambda e: (self.message_entry.delete(0, tk.END) if self.message_entry.get() == "Type your message here..." else None, self.message_entry.config(fg="black")))
        self.message_entry.bind("<FocusOut>", lambda e: (self.message_entry.insert(0, "Type your message here...") if not self.message_entry.get() else None, self.message_entry.config(fg="grey")))
        self.message_entry.bind("<Return>", lambda event: self.send_message())

        self.send_button = tk.Button(message_frame, text="►", font=("Arial", 10), bg="#4caf50", fg="white", bd=0, padx=10, pady=5, relief="ridge", activebackground="#45a049", command=self.send_message)
        self.send_button.pack(side=tk.RIGHT, padx=(10, 0))

        connection_frame = tk.Frame(self.root, bg="#f7f7f7", bd=2, relief="groove")
        connection_frame.pack(pady=20, padx=20, fill=tk.X)

        title_frame = tk.Frame(connection_frame, bg="#f7f7f7")
        title_frame.pack()

        connection_title = tk.Label(title_frame, text="Connection Settings", font=("Arial", 14, "bold"), bg="#f7f7f7")
        connection_title.pack(side=tk.LEFT, pady=10)

        self.show_ip_label = tk.Label(title_frame, text="🌐", font=("Arial", 12), bg="#f7f7f7", fg="#4caf50", cursor="hand2")
        self.show_ip_label.pack(side=tk.LEFT, padx=5)  

        self.show_ip_label.bind("<Enter>", self.show_ip)
        self.show_ip_label.bind("<Leave>", self.hide_ip)

        self.show_ports_label = tk.Label(title_frame, text="🔌", font=("Arial", 12), bg="#f7f7f7", fg="#4caf50", cursor="hand2")
        self.show_ports_label.pack(side=tk.LEFT, padx=5) 

        self.show_ports_label.bind("<Enter>", self.show_ports)
        self.show_ports_label.bind("<Leave>", self.hide_ports)

        self.device_info_label = tk.Label(title_frame, text="💻", font=("Arial", 12), bg="#f7f7f7", fg="#4caf50", cursor="hand2")
        self.device_info_label.pack(side=tk.LEFT, padx=5)  

        self.device_info_label.bind("<Enter>", self.show_device_info)
        self.device_info_label.bind("<Leave>", self.hide_device_info)

        self.firewall_status_label = tk.Label(title_frame, text="🛡️", font=("Arial", 12), bg="#f7f7f7", fg="#4caf50", cursor="hand2")
        self.firewall_status_label.pack(side=tk.LEFT, padx=5)  

        self.firewall_status_label.bind("<Enter>", self.show_firewall_status)
        self.firewall_status_label.bind("<Leave>", self.hide_firewall_status)

        input_frame = tk.Frame(connection_frame, bg="#f7f7f7")
        input_frame.pack()

        self.listen_port_label = tk.Label(input_frame, text="Listening Port:", font=("Arial", 12), bg="#f7f7f7")
        self.listen_port_label.pack(side=tk.LEFT, padx=5, pady=5)
        self.listen_port_entry = tk.Entry(input_frame, font=("Arial", 12), bg="#ffffff", bd=1, relief="solid", width=10)
        self.listen_port_entry.pack(side=tk.LEFT, padx=5, pady=5)
        self.listen_port_entry.insert(0, '5000')

        self.target_ip_label = tk.Label(input_frame, text="Target IP:", font=("Arial", 12), bg="#f7f7f7")
        self.target_ip_label.pack(side=tk.LEFT, padx=5, pady=5)
        self.target_ip_entry = tk.Entry(input_frame, font=("Arial", 12), bg="#ffffff", bd=1, relief="solid", width=14)
        self.target_ip_entry.pack(side=tk.LEFT, padx=5, pady=5)
        self.target_ip_entry.insert(0, '127.0.0.1')

        self.target_port_label = tk.Label(input_frame, text="Target Port:", font=("Arial", 12), bg="#f7f7f7")
        self.target_port_label.pack(side=tk.LEFT, padx=5, pady=5)
        self.target_port_entry = tk.Entry(input_frame, font=("Arial", 12), bg="#ffffff", bd=1, relief="solid", width=10)
        self.target_port_entry.pack(side=tk.LEFT, padx=5, pady=5)
        self.target_port_entry.insert(0, '5000')

        button_frame = tk.Frame(self.root, bg="#f7f7f7")
        button_frame.pack(pady=10)

        self.connect_button = tk.Button(button_frame, text="Start Connection", font=("Arial", 12), bg="#4caf50", fg="white", bd=0, command=self.toggle_connection)
        self.connect_button.pack(side=tk.LEFT, padx=10, pady=5)

        self.exit_button = tk.Button(button_frame, text="Exit", font=("Arial", 12), bg="#f44336", fg="white", bd=0, command=self.exit_application)
        self.exit_button.pack(side=tk.LEFT, padx=10, pady=5)

        self.options_button = tk.Button(button_frame, text="Options", font=("Arial", 12), bg="#ff9800", fg="white", bd=0, command=self.open_options_window)
        self.options_button.pack(side=tk.LEFT, padx=10, pady=5)

        self.help_button = tk.Button(button_frame, text="Help", font=("Arial", 12), bg="#2196f3", fg="white", bd=0, command=self.open_help_window)
        self.help_button.pack(side=tk.LEFT, padx=10, pady=5)

        self.root.bind("<s>", lambda event: self.toggle_connection() if not self.is_input_focused() else None)  
        self.root.bind("<o>", lambda event: self.open_options_window() if not self.is_input_focused() else None)  
        self.root.bind("<e>", lambda event: self.exit_application() if not self.is_input_focused() else None)  
        self.root.bind("<h>", lambda event: self.open_help_window() if not self.is_input_focused() else None) 
        self.root.bind("<f>", lambda event: self.toggle_fullscreen() if not self.is_input_focused() else None)  

 
        self.status_frame = tk.Frame(self.root, bg="#f0f0f0", bd=1, relief="flat")
        self.status_frame.pack(fill=tk.X, padx=10, pady=10)


        self.connection_status = "Disconnected"
        self.status_label = tk.Label(self.status_frame, text=self.connection_status, font=("Arial", 12, "bold"), bg="#f0f0f0", anchor="w", fg="red", padx=10)
        self.status_label.pack(side=tk.LEFT, fill=tk.X)

        self.handshake_progress = tk.Label(self.status_frame, text="Handshake Progress: 0/3", font=("Arial", 12), bg="#f0f0f0", anchor="w", padx=10)
        self.handshake_progress.pack(side=tk.RIGHT)

    def update_heartbeat_area_color(self, in_progress):
            new_color = self.heartbeat_area_fragment_bg if in_progress else self.heartbeat_area_default_bg
            self.heartbeat_area.configure(bg=new_color)

            if in_progress:
                self.display_heartbeat("Fragmentation started", message_type="special")  
            else:
                self.display_heartbeat("Fragmentation stopped", message_type="special")  


    def is_input_focused(self):
            """ Check if the message input field is focused """
            return self.message_entry.focus_get() is self.message_entry

    def unfocus_input_field(self, event):
        """Remove focus from the input field if clicking outside of it."""
        if not self.message_entry.winfo_contains(event.x_root, event.y_root):
            self.message_entry.focus_out() 



    def update_handshake_progress(self, stage):
        if stage < 3:
            self.handshake_progress.config(text=f"Handshake Progress: {stage}/3")
            self.handshake_progress.pack(side=tk.RIGHT)  
        else:
            self.handshake_progress.pack_forget() 

    def update_status(self):
        """Update the status bar with the current connection status and max fragment size."""
        if "Connected" in self.connection_status:
            self.status_label.config(text=f"{self.connection_status} | Max Message: {BUFFER_SIZE} bytes | Max File: {FILE_BUFFER_SIZE} bytes", fg="green")
        else:
            self.status_label.config(text=self.connection_status, fg="red")

    def show_ip(self, event):
        """Display local IP addresses and network details as a tooltip when hovering over the question mark"""
        network_info = self.get_network_info()
        ip_text = "My Network Info:\n" + "\n".join(network_info)

        self.tooltip = tk.Toplevel(self.root)
        self.tooltip.wm_overrideredirect(True)  
        self.tooltip.config(bg="#ffffe0")  

        tooltip_label = tk.Label(self.tooltip, text=ip_text, font=("Arial", 10, "italic"), fg="gray", bg="#ffffe0", bd=1, relief="solid")
        tooltip_label.pack(padx=5, pady=5)

        x = event.x_root + 10  
        y = event.y_root + 10  

        self.tooltip.geometry(f"+{x}+{y}")

        self.tooltip.lift()  
        self.tooltip.attributes("-topmost", 1)  

    def hide_ip(self, event):
        """Hide the IP address tooltip"""
        if hasattr(self, 'tooltip'):
            self.tooltip.destroy()
            del self.tooltip

    def get_network_info(self):
        """Get network name (SSID) and local IP addresses in a cross-platform way."""
        network_info = []

        system_name = platform.system()
        try:
            if system_name == "Windows":
                ssid_command = ['netsh', 'wlan', 'show', 'interfaces']
                ip_command = ['ipconfig']
            elif system_name == "Darwin":  # macOS
                ssid_command = ['/System/Library/PrivateFrameworks/Apple80211.framework/Versions/Current/Resources/airport', '-I']
                ip_command = ['ifconfig']
            elif system_name == "Linux":
                ssid_command = ['nmcli', '-t', '-f', 'active,ssid', 'dev', 'wifi']
                ip_command = ['ip', '-4', 'addr']
            else:
                raise NotImplementedError(f"Unsupported OS: {system_name}")

            try:
                ssid_result = subprocess.run(
                    ssid_command,
                    capture_output=True,
                    text=True,
                    check=True,
                    encoding='utf-8',  
                    errors='replace',  
                    creationflags=subprocess.CREATE_NO_WINDOW  
                )
                ssid_output = ssid_result.stdout
                if system_name == "Windows":
                    for line in ssid_output.splitlines():
                        if "SSID" in line and "BSSID" not in line:
                            ssid = line.split(":")[1].strip()
                            network_info.append(f"Network Name (SSID): {ssid}")
                            break
                elif system_name == "Darwin":
                    for line in ssid_output.splitlines():
                        if "SSID" in line:
                            ssid = line.split(":")[1].strip()
                            network_info.append(f"Network Name (SSID): {ssid}")
                            break
                elif system_name == "Linux":
                    for line in ssid_output.splitlines():
                        if line.startswith("yes"):
                            ssid = line.split(":")[1]
                            network_info.append(f"Network Name (SSID): {ssid}")
                            break
            except Exception as e:
                network_info.append(f"(Turn on location access to show network name)")

            try:
                ip_result = subprocess.run(
                    ip_command,
                    capture_output=True,
                    text=True,
                    check=True,
                    encoding='utf-8', 
                    errors='replace',  
                    creationflags=subprocess.CREATE_NO_WINDOW  
                )
                ip_output = ip_result.stdout
                if system_name == "Windows":
                    for line in ip_output.splitlines():
                        if "IPv4" in line:
                            network_info.append(line.strip())
                elif system_name in ["Darwin", "Linux"]:
                    for line in ip_output.splitlines():
                        if "inet " in line and not line.strip().startswith("127."):
                            ip_address = line.split()[1]
                            network_info.append(f"Local IP Address: {ip_address}")
            except Exception as e:
                network_info.append(f"Error retrieving IP addresses: {str(e)}")

        except NotImplementedError as e:
            network_info.append(str(e))
        except Exception as e:
            network_info.append(f"An unexpected error occurred: {str(e)}")

        if not network_info:
            network_info.append("No network information could be retrieved.")

        return network_info

    def show_ports(self, event):
        """Display available ports when hovering over the question mark"""
        available_ports = self.get_available_ports()

        ports_text = "Available Ports:\n" + self.format_ports(available_ports)

        ports_text += "\n\n(Note: These are just some example available ports.)\n" \
                    "Suggested Ports for use: 8080, 3000, 5000"

        self.tooltip_ports = tk.Toplevel(self.root)
        self.tooltip_ports.wm_overrideredirect(True)  
        self.tooltip_ports.config(bg="#ffffe0")  

        tooltip_label = tk.Label(self.tooltip_ports, text=ports_text, font=("Arial", 10, "italic"), fg="gray", bg="#ffffe0", bd=1, relief="solid")
        tooltip_label.pack(padx=5, pady=5)

        x = event.x_root + 10  
        y = event.y_root + 10  

        self.tooltip_ports.geometry(f"+{x}+{y}")

        self.tooltip_ports.lift()  
        self.tooltip_ports.attributes("-topmost", 1)  

    def hide_ports(self, event):
        """Hide the available ports tooltip"""
        if hasattr(self, 'tooltip_ports'):
            self.tooltip_ports.destroy()
            del self.tooltip_ports


    def get_available_ports(self, limit=50):
        """Get a list of available ports."""
        available_ports = []
        start_port = 1024  
        end_port = 65536  

        for port in range(start_port, end_port):
            with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as sock:
                try:
                    sock.bind(("", port))
                    available_ports.append(port)
                    if len(available_ports) >= limit:  
                        break
                except OSError:
                    continue  

        return available_ports
  
    def format_ports(self, ports):
        """Format the list of ports into a string representation"""
        if not ports:
            return "No available ports"
        
        formatted_ports = []
        start = ports[0]
        end = ports[0]

        for i in range(1, len(ports)):
            if ports[i] == end + 1:  
                end = ports[i]
            else:
                if start == end:
                    formatted_ports.append(f"{start}") 
                else:
                    formatted_ports.append(f"{start} - {end}")  
                start = end = ports[i]  

        if start == end:
            formatted_ports.append(f"{start}")
        else:
            formatted_ports.append(f"{start} - {end}")

        return ", ".join(formatted_ports)  

    def show_firewall_status(self, event):
        """Display firewall status when hovering over the shield icon."""
        firewall_status = self.check_firewall_status()  
        status_text = f"Firewall Status: {firewall_status}"

        self.tooltip_firewall = tk.Toplevel(self.root)
        self.tooltip_firewall.wm_overrideredirect(True) 
        self.tooltip_firewall.config(bg="#ffffe0") 

        tooltip_label = tk.Label(self.tooltip_firewall, text=status_text, font=("Arial", 10, "italic"), fg="gray", bg="#ffffe0", bd=1, relief="solid")
        tooltip_label.pack(padx=5, pady=5)

        x = event.x_root + 10  
        y = event.y_root + 10  

        self.tooltip_firewall.geometry(f"+{x}+{y}")

        self.tooltip_firewall.lift() 
        self.tooltip_firewall.attributes("-topmost", 1)  

    def hide_firewall_status(self, event):
        if hasattr(self, 'tooltip_firewall'):
            self.tooltip_firewall.destroy()
            del self.tooltip_firewall

    def check_firewall_status(self):
        try:
            system_name = platform.system()

            if system_name == "Windows":
                cmd = 'netsh advfirewall show allprofiles'

                creation_flags = subprocess.CREATE_NO_WINDOW
                output = subprocess.check_output(cmd, shell=True, text=True, creationflags=creation_flags)

                for line in output.splitlines():
                    match = re.search(r'State\s+(\w+)', line)
                    if match:
                        state = match.group(1)
                        if state.upper() == "ON":
                            return "Enabled"
                        elif state.upper() == "OFF":
                            return "Disabled"

                return "Unknown"

            elif system_name == "Darwin":  # macOS
                cmd = 'pfctl -s info'
                output = subprocess.check_output(cmd, shell=True, text=True)
                if "Status: Enabled" in output:
                    return "Enabled"
                elif "Status: Disabled" in output:
                    return "Disabled"
                else:
                    return "Unknown"

            elif system_name == "Linux":
                cmd = 'ufw status'
                output = subprocess.check_output(cmd, shell=True, text=True)
                if "Status: active" in output:
                    return "Enabled"
                elif "Status: inactive" in output:
                    return "Disabled"
                else:
                    return "Unknown"

            else:
                return f"Unsupported OS: {system_name}"

        except Exception as e:
            return f"Error: {str(e)}"

    def show_device_info(self, event):
        device_info = self.get_device_info()
        
        self.tooltip_device_info = tk.Toplevel(self.root)
        self.tooltip_device_info.wm_overrideredirect(True) 
        self.tooltip_device_info.config(bg="#ffffe0")
        
        tooltip_label = tk.Label(self.tooltip_device_info, text=device_info, font=("Arial", 10, "italic"), fg="gray", bg="#ffffe0", bd=1, relief="solid")
        tooltip_label.pack(padx=5, pady=5)
        
        x = event.x_root + 10  
        y = event.y_root + 10  
        
        self.tooltip_device_info.geometry(f"+{x}+{y}")
        
        self.tooltip_device_info.lift()  
        self.tooltip_device_info.attributes("-topmost", 1)  

    def hide_device_info(self, event):
        if hasattr(self, 'tooltip_device_info'):
            self.tooltip_device_info.destroy()
            del self.tooltip_device_info


    def get_device_info(self):
        try:
            hostname = platform.node() or "Unknown"
            os_info = platform.system() + " " + platform.release()
            architecture = platform.architecture()[0] or "Unknown"
            cpu_info = platform.processor() or "Unknown"
            python_version = platform.python_version()

            try:
                memory_info = psutil.virtual_memory()
                total_memory = self.format_bytes(memory_info.total)
                available_memory = self.format_bytes(memory_info.available)
            except ImportError:
                total_memory = "N/A"
                available_memory = "N/A"

            try:
                disk_info = psutil.disk_usage('/')
                total_disk = self.format_bytes(disk_info.total)
                free_disk = self.format_bytes(disk_info.free)
            except ImportError:
                total_disk = "N/A"
                free_disk = "N/A"

            logical_cores = psutil.cpu_count(logical=True)
            physical_cores = psutil.cpu_count(logical=False)

            boot_time = datetime.fromtimestamp(psutil.boot_time())
            uptime = datetime.now() - boot_time
            uptime_str = str(uptime).split('.')[0] 

            ip_address = "Unknown"
            mac_address = "Unknown"
            try:
                hostname = socket.gethostname()
                ip_address = socket.gethostbyname(hostname)
                
                for iface in psutil.net_if_addrs().values():
                    for addr in iface:
                        if addr.family == psutil.AF_LINK:
                            mac_address = addr.address
                            break
            except Exception:
                pass

            device_info = (
                f"Device: {hostname}\n"
                f"OS: {os_info}\n"
                f"Architecture: {architecture}\n"
                f"CPU: {cpu_info}\n"
                f"Logical Cores: {logical_cores}, Physical Cores: {physical_cores}\n"
                f"Total Memory: {total_memory}\n"
                f"Available Memory: {available_memory}\n"
                f"Total Disk: {total_disk}\n"
                f"Free Disk: {free_disk}\n"
                f"IP Address: {ip_address}\n"
                f"MAC Address: {mac_address}\n"
                f"Uptime: {uptime_str}\n"
                f"Python Version: {python_version}"
            )

        except Exception as e:
            device_info = f"Error fetching device info: {str(e)}"

        return device_info

    def format_bytes(self, size):
        """Convert bytes to a more readable format (KB, MB, GB)."""
        if size < 1024:
            return f"{size} Bytes"
        elif size < 1048576:
            return f"{size / 1024:.2f} KB"
        elif size < 1073741824:
            return f"{size / 1048576:.2f} MB"
        else:
            return f"{size / 1073741824:.2f} GB"

    def preload_images(self):
        base_path = getattr(sys, '_MEIPASS', os.path.dirname(__file__))

        sender_image_path = os.path.join(base_path, "profilovky", "1.png")
        sender_image = Image.open(sender_image_path).resize((30, 30), Image.LANCZOS)

        self.sender_image_with_bg = Image.new("RGBA", (40, 40), "#98FB98") 

        draw = ImageDraw.Draw(self.sender_image_with_bg)
        draw.ellipse((0, 0, 39, 39), outline="black", width=1)  

        self.sender_image_with_bg.paste(sender_image, (5, 5), sender_image)  
        self.sender_photo = ImageTk.PhotoImage(self.sender_image_with_bg)

        receiver_image_path = os.path.join(base_path, "profilovky", "5.png")
        receiver_image = Image.open(receiver_image_path).resize((30, 30), Image.LANCZOS)

        self.receiver_image_with_bg = Image.new("RGBA", (40, 40), "#CCE6FF")  

        draw = ImageDraw.Draw(self.receiver_image_with_bg)
        draw.ellipse((0, 0, 39, 39), outline="black", width=1)  

        self.receiver_image_with_bg.paste(receiver_image, (5, 5), receiver_image)  
        self.receiver_photo = ImageTk.PhotoImage(self.receiver_image_with_bg)

        

    def display_message(self, message, sender="You", total_fragments=None, elapsed_time=None, image_file=None, audio_file=None, doc_file=None, video_file=None):

        self.chat_area.configure(state='normal')

        network_info = self.get_network_info()
        local_ip_address = None

        for info in network_info:
            if "IPv4" in info:
                local_ip_address = info.split(":")[1].strip() 
                break

        is_user_message = (sender == "You" or sender == local_ip_address)        

        if is_user_message and self.show_ip_address.get():
            sender = local_ip_address  

        sender_info = f"{sender} "
        formatted_message = f"{message}\n"

        current_time = datetime.now().strftime("%H:%M:%S") 
        local_ip_from_network = None
        if network_info and len(network_info) > 0 and ":" in network_info[0]:
            local_ip_from_network = network_info[0].split(":")[1].strip()

        if sender == "You" or (local_ip_from_network and sender == local_ip_from_network):
            formatted_time = f"Sent at {current_time}"
        else:
            formatted_time = f"Received at {current_time}"


        if total_fragments is not None and self.show_total_fragments.get():
            formatted_time += f" | Fragments: {total_fragments}"

        if elapsed_time is not None and elapsed_time > 0.00 and self.show_elapsed_time.get():
            formatted_time += f" | Duration: {elapsed_time:.2f} s"

        font_size_mapping = {
            "Very Small": 8,
            "Small": 10,
            "Normal": 12,
            "Large": 14,
            "Very Large": 16
        }
        selected_size = font_size_mapping[self.selected_font_size.get()]

        current_family = self.selected_font_family.get()            

        if is_user_message:
            image_to_use = self.sender_photo
            bg_color = "#98FB98"
            text_color = "black"
            self.chat_area.image_create(tk.END, image=image_to_use)
            self.chat_area.insert(tk.END, f"    {sender_info}", "user_info")  
            self.chat_area.insert(tk.END, formatted_message, "user_message")

            self.chat_area.insert(tk.END, f"    {formatted_time}\n", "message_time")  
        elif sender == "System":
            self.chat_area.insert(tk.END, f"    {sender_info}", "system_info")  
            self.chat_area.insert(tk.END, formatted_message, "system_message")

            self.chat_area.insert(tk.END, f"    {formatted_time}\n", "message_time_system")  
        else:
            image_to_use = self.receiver_photo
            bg_color = "#cce6ff"
            text_color = "black"
            self.chat_area.insert(tk.END, f"   ", "right_align_space")  
            self.chat_area.image_create(tk.END, image=image_to_use) 
            self.chat_area.insert(tk.END, f"{sender_info}", "other_info")  
            self.chat_area.insert(tk.END, formatted_message, "other_message")  

            self.chat_area.insert(tk.END, f"    {formatted_time}\n", "message_time_other") 

            if self.show_desktop_notification.get():  
                self.show_notification(sender, message)

            if image_file:
                try:
                    image = Image.open(image_file)
                    original_width, original_height = image.size
                    
                    max_size = 200
                    
                    if original_width > original_height:
                        new_width = max_size
                        new_height = int((max_size / original_width) * original_height)
                    else:
                        new_height = max_size
                        new_width = int((max_size / original_height) * original_width)
                    
                    image = image.resize((new_width, new_height), Image.Resampling.LANCZOS)
                    
                    photo = ImageTk.PhotoImage(image)

                    start_position = self.chat_area.index(tk.END)
                    self.chat_area.insert(tk.END, "\n")

                    self.chat_area.image_create(tk.END, image=photo)  
                    self.chat_area.image = photo  

                    self.chat_area.insert(tk.END, "\n")
                    end_position = self.chat_area.index(tk.END + "-1c") 

                    self.chat_area.tag_add("image_tag", start_position, end_position)

                    self.chat_area.tag_bind("image_tag", "<Button-1>", lambda event, img_path=image_file: self.open_image_full_size(img_path))



                except Exception as e:
                    self.logger.error(f"Error preloading logo: {e}")


            if audio_file:
                try:                    
                    def toggle_audio_button():
                        if self.is_playing:
                            self.play_audio(audio_file) 
                            play_button.config(text="Play Audio")  
                        else:
                            self.play_audio(audio_file)  
                            play_button.config(text="Pause Audio")  
                    
                    play_button = tk.Button(self.chat_area, text="Play Audio", command=toggle_audio_button, relief="raised", bg="#4CAF50", fg="white", padx=10, pady=5, font=("Helvetica", 10, "bold"))

                    button_start_position = self.chat_area.index(tk.END)
                    self.chat_area.insert(tk.END, "\n")

                    self.chat_area.window_create(tk.END, window=play_button)

                    self.chat_area.insert(tk.END, "\n")
                    button_end_position = self.chat_area.index(tk.END + "-1c") 

                    self.chat_area.tag_add("audio_tag", button_start_position, button_end_position)

                except Exception as e:
                    self.logger.error(f"Error preloading audio: {e}")


            if doc_file:
                try:     
                     
                    open_button = tk.Button(self.chat_area, text="Open File", command=lambda: self.open_document(doc_file), relief="raised", bg="#4CAF50", fg="white", padx=10, pady=5, font=("Helvetica", 10, "bold"))

                    button_start_position = self.chat_area.index(tk.END)
                    self.chat_area.insert(tk.END, "\n")

                    self.chat_area.window_create(tk.END, window=open_button)

                    self.chat_area.insert(tk.END, "\n")
                    button_end_position = self.chat_area.index(tk.END + "-1c")  

                    self.chat_area.tag_add("doc_tag", button_start_position, button_end_position)

                except Exception as e:
                    self.logger.error(f"Error preloading audio: {e}")


            if video_file: 
                try:
                    cap = cv2.VideoCapture(video_file)
                    ret, frame = cap.read()
                    if ret:
                        thumbnail = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
                        thumbnail_image = Image.fromarray(thumbnail)
                        thumbnail_width, thumbnail_height = 200, 150  
                        thumbnail_image = thumbnail_image.resize((thumbnail_width, thumbnail_height), Image.Resampling.LANCZOS)

                        photo = ImageTk.PhotoImage(thumbnail_image)

                        thumbnail_label = tk.Label(self.chat_area, image=photo)

                        def on_click(event):
                            self.play_video(video_file, thumbnail_label)  

                        thumbnail_label.bind("<Button-1>", on_click)

                        thumbnail_start_position = self.chat_area.index(tk.END)
                        self.chat_area.insert(tk.END, "\n")

                        self.chat_area.window_create(tk.END, window=thumbnail_label)

                        self.chat_area.insert(tk.END, "\n")
                        thumbnail_end_position = self.chat_area.index(tk.END + "-1c") 

                        self.chat_area.tag_add("video_tag", thumbnail_start_position, thumbnail_end_position)

                        self.chat_area.image = photo 

                    cap.release()
                except Exception as e:
                    self.logger.error(f"Failed to load video thumbnail: {e}")

        self.chat_area.insert(tk.END, "\n")  

        self.chat_area.yview(tk.END)

        self.chat_area.configure(state='disabled')

        self.chat_area.tag_configure("user_info", 
                                    foreground="darkgreen", 
                                    background="#98FB98", 
                                    justify="left",
                                    font=(current_family, selected_size - 2, "italic"),  
                                    spacing1=2,
                                    spacing2=2,
                                    spacing3=2,
                                    wrap="word")

        self.chat_area.tag_configure("user_message", 
                                    foreground="black", 
                                    background="#98FB98", 
                                    justify="left",
                                    font=(current_family, selected_size, "normal"),
                                    spacing1=5,
                                    spacing2=5,
                                    spacing3=5,
                                    wrap="word")

        self.chat_area.tag_configure("other_info", 
                                    foreground="darkblue",
                                    background="#cce6ff", 
                                    justify="right",
                                    font=(current_family, selected_size - 2, "italic"),  
                                    spacing1=2,
                                    spacing2=2,
                                    spacing3=2,
                                    wrap="word")

        self.chat_area.tag_configure("other_message", 
                                    foreground="black", 
                                    background="#cce6ff", 
                                    justify="right",
                                    font=(current_family, selected_size, "normal"),
                                    spacing1=5,
                                    spacing2=5,
                                    spacing3=5,
                                    wrap="word")

        self.chat_area.tag_configure("right_align_space", 
                                    foreground="black", 
                                    background="#cce6ff",  
                                    justify="right", 
                                    font=(current_family, selected_size, "normal"), 
                                    spacing1=0,  
                                    spacing2=0, 
                                    spacing3=0, 
                                    wrap="word")

        self.chat_area.tag_configure("image_tag", 
                             foreground="black", 
                             background="#cce6ff", 
                             justify="right", 
                             spacing1=5,      
                             spacing2=5,      
                             spacing3=5,      
                             wrap="word")  
        
        self.chat_area.tag_configure("audio_tag", 
                             foreground="black", 
                             background="#cce6ff", 
                             justify="right",  
                             spacing1=5,        
                             spacing2=5,       
                             spacing3=5,        
                             wrap="word")  

        self.chat_area.tag_configure("doc_tag", 
                             foreground="black", 
                             background="#cce6ff", 
                             justify="right",  
                             spacing1=5,       
                             spacing2=5,       
                             spacing3=5,       
                             wrap="word")  

        self.chat_area.tag_configure("video_tag", 
                             foreground="black", 
                             background="#cce6ff", 
                             justify="right",  
                             spacing1=5,       
                             spacing2=5,        
                             spacing3=5,        
                             wrap="word")  

        self.chat_area.tag_configure("system_info", 
                                foreground="purple",  
                                background="#D3D3FF",  
                                justify="center",
                                font=(current_family, selected_size - 2, "italic"),
                                spacing1=2,
                                spacing2=2,
                                spacing3=2,
                                wrap="word")

        self.chat_area.tag_configure("system_message", 
                                foreground="black", 
                                background="#D3D3FF", 
                                justify="center",
                                font=(current_family, selected_size, "normal"),
                                spacing1=5,
                                spacing2=5,
                                spacing3=5,
                                wrap="word")

        self.chat_area.tag_configure("message_time", 
                                    foreground="grey", 
                                    background="#98FB98", 
                                    justify="left",
                                    font=(current_family, selected_size - 2, "italic"),  
                                    spacing1=2,
                                    spacing2=2,
                                    spacing3=2,
                                    wrap="word")

        self.chat_area.tag_configure("message_time_other", 
                                    foreground="grey", 
                                    background="#cce6ff",  
                                    justify="right",
                                    font=(current_family, selected_size - 2, "italic"),
                                    spacing1=2,
                                    spacing2=2,
                                    spacing3=2,
                                    wrap="word")

        self.chat_area.tag_configure("message_time_system", 
                                foreground="grey", 
                                background="#D3D3FF",  
                                justify="center",
                                font=(current_family, selected_size - 2, "italic"),
                                spacing1=2,
                                spacing2=2,
                                spacing3=2,
                                wrap="word")

    def open_image_full_size(self, img_path):
        """Open the image in a new window at full size when clicked."""
        try:
            image = Image.open(img_path)
            
            full_size_window = tk.Toplevel(self.root)
            full_size_window.title("Full Size Image")
            
            full_size_image = ImageTk.PhotoImage(image)
            
            image_label = tk.Label(full_size_window, image=full_size_image)
            image_label.pack()
            
            image_label.image = full_size_image

        except Exception as e:
            self.logger.error(f"Failed to open image in full size: {e}")


    def play_audio(self, audio_file):
        try:
            if os.path.exists(audio_file):
                if not self.is_playing:
                    pygame.mixer.init()  
                    pygame.mixer.music.load(audio_file)  
                    pygame.mixer.music.play()  
                    self.is_playing = True  
                    self.audio_file = audio_file  
                else:
                    pygame.mixer.music.pause()  
                    self.is_playing = False  
            else:
                self.logger.error(f"Audio file {audio_file} does not exist.")

        except Exception as e:
            self.logger.error(f"Failed to play audio: {e}")


  
    def open_document(self, doc_file):
        try:
            os.startfile(doc_file)  
        except Exception as e:
            self.logger.error(f"Failed to open document: {e}")

    def play_video(self, video_file, label):
        try:
            if os.path.exists(video_file):
                if self.is_playing_video:
                    self.stop_video()

                self.is_playing_video = True
                self.video_file = video_file
                self.cap = cv2.VideoCapture(video_file)

                def video_loop():
                    while self.is_playing_video:
                        ret, frame = self.cap.read()
                        if not ret:
                            break 

                        frame_rgb = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
                        img = Image.fromarray(frame_rgb)

                        img = img.resize((200, 150), Image.Resampling.LANCZOS)

                        photo = ImageTk.PhotoImage(img)

                        label.config(image=photo)
                        label.image = photo 

                        cv2.waitKey(1)

                    self.cap.release()
                    self.is_playing_video = False  
                    self.logger.normal(f"Video playback finished.")

                self.video_thread = threading.Thread(target=video_loop)
                self.video_thread.start()
            else:
                self.logger.error(f"Video file {video_file} does not exist.")
        except Exception as e:
            self.logger.error(f"Failed to play video: {e}")

    def wrap_text(self, text, max_length):
        lines = []
        while len(text) > max_length:
            cut_position = text.rfind(' ', 0, max_length)
            if cut_position == -1:
                cut_position = max_length
            lines.append(text[:cut_position].strip())
            text = text[cut_position:].strip()
        if text:
            lines.append(text)
        return lines 

    def show_notification(self, sender, message):
        notification_window = tk.Toplevel(self.root)
        notification_window.overrideredirect(True)  
        notification_window.wm_attributes("-topmost", 1) 
        notification_window.transient()  

        logo_photo = self.logo_photo
       
        notification_window.protocol("WM_DELETE_WINDOW", notification_window.destroy)

        screen_width = self.root.winfo_screenwidth()
        screen_height = self.root.winfo_screenheight()

        max_characters = 150
        if len(message) > max_characters:
            message = message[:max_characters] + "..."  

        wrapped_lines = self.wrap_text(message, 30)
        message_text = "\n".join(wrapped_lines)

        notification_width = 300  
        line_count = message_text.count("\n") + 1
        line_height = 18  
        notification_height = max(line_count * line_height + 30, 90)

        notification_position_x = screen_width - notification_width - 20
        notification_position_y = screen_height - notification_height - 60

        notification_window.geometry(f"{notification_width}x{notification_height}+{notification_position_x}+{notification_position_y}")

        frame = tk.Frame(notification_window, bg="#4C9FD9")
        frame.pack(fill='x', padx=5, pady=5)

        if logo_photo:
            logo_label = tk.Label(frame, image=logo_photo, bg="#4C9FD9")
            logo_label.image = logo_photo 
            logo_label.pack(side='left', padx=5)

        sender_label = tk.Label(frame, 
                                text=f"Message from {sender}", 
                                bg="#4C9FD9", 
                                fg="#D3D3D3",  
                                font=("Arial", 11, "bold"), 
                                anchor="w",  
                                justify="left")
        sender_label.pack(side='left', padx=5)  

        notification_label = tk.Label(notification_window, 
                                    text=message_text, 
                                    bg="#4C9FD9", 
                                    fg="white", 
                                    font=("Arial", 9), 
                                    anchor="nw", 
                                    justify="left",
                                    padx=5, pady=0)  
        notification_label.pack(fill='both', expand=True)

        notification_window.config(bg="#4C9FD9")

        notification_window.update_idletasks()  
        notification_window.wm_attributes('-alpha', 0)  

        def fade_in(alpha=0):
            if alpha < 0.8:  
                alpha += 0.05  
                notification_window.wm_attributes('-alpha', alpha)
                notification_window.after(20, fade_in, alpha) 
            else:
                notification_window.wm_attributes('-alpha', 0.8)  

        notification_window.after(50, fade_in)

        def fade_out(alpha=0.8):
            if alpha > 0:
                notification_window.wm_attributes('-alpha', alpha)
                notification_window.after(50, fade_out, alpha - 0.05) 
            else:
                notification_window.destroy() 
        notification_window.after(5000, fade_out)


    def display_heartbeat(self, message, message_type="sent"):
        self.heartbeat_area.configure(state='normal')

        current_time = datetime.now().strftime("%H:%M:%S")

        if message_type == "sent":
            formatted_message = f"  ⮞   {current_time}  \n"  
            self.heartbeat_area.insert(tk.END, formatted_message, "sent_heartbeat")  
        elif message_type == "received":
            formatted_message = f"   {current_time}  ⮜   \n" 
            self.heartbeat_area.insert(tk.END, formatted_message, "received_heartbeat") 
        elif message_type == "special":
            formatted_message = f"{message} at {current_time}\n" if message else f"{current_time}\n"
            self.heartbeat_area.insert(tk.END, formatted_message, "special_heartbeat")


        self.heartbeat_area.tag_configure("sent_heartbeat", 
                                        foreground="black", 
                                        font=("Arial", 10, "normal"),
                                        justify="left") 

        self.heartbeat_area.tag_configure("received_heartbeat", 
                                        foreground="black", 
                                        font=("Arial", 10, "normal"),
                                        justify="right") 

        self.heartbeat_area.tag_configure("special_heartbeat", 
                                          foreground="blue", 
                                          font=("Arial", 10, "bold"),
                                          justify="center")  
                                        
        self.heartbeat_area.insert(tk.END, "\n")  
        self.heartbeat_area.configure(state='disabled')
        self.heartbeat_area.yview(tk.END)

    def reset_elapsed_time(self):
        self.elapsed_time = 0
        self.heartbeat_interval_label.config(text=f"🕒 {self.elapsed_time:.0f} s")
    
    def stop_elapsed_time_display(self):
        if self.elapsed_time_job is not None:
            self.root.after_cancel(self.elapsed_time_job)  
            self.elapsed_time_job = None  

        self.elapsed_time = 0  
        self.heartbeat_interval_label.config(text=f"🕒 {self.elapsed_time:.0f} s")

    def update_elapsed_time_display(self):
        if self.node:
            if self.elapsed_time >= 0:
                self.elapsed_time += 1  
                self.heartbeat_interval_label.config(text=f"🕒 {self.elapsed_time:.0f} s")
        
        self.elapsed_time_job = self.root.after(1000, self.update_elapsed_time_display) 

    def send_message(self):
        message = self.message_entry.get()

        if not self.node:
            if self.show_system_messages.get():
                self.display_message("Warning. Please start a connection first.", sender="System")
            return

        if message and message != "Type your message here...":
            self.node.send_message(message)
            self.display_message(message, sender="You") 
            self.message_entry.delete(0, tk.END)  
        else:
            if self.show_system_messages.get():
                self.display_message("Warning. Please enter a valid message.", sender="System")

    def send_file(self):
        if not self.node or not self.node.running:  
            if self.show_system_messages.get():
                self.display_message("Warning: Please start a connection first.", sender="System")
            return

        file_path = filedialog.askopenfilename(title="Select a file to send")  
        if file_path:  
            self.display_message(f"Selected file: {file_path}", sender="System")

            try:
                self.node.send_file(file_path)

            except Exception as e:
                self.display_message(f"Error sending file: {str(e)}", sender="System")
        else:
            self.display_message("No file selected.", sender="System")

    def toggle_connection(self):
        if self.node is None or not self.node.running:
            try:
                listen_port = int(self.listen_port_entry.get())
                target_ip = self.target_ip_entry.get()
                target_port = int(self.target_port_entry.get())

                if not listen_port or not target_ip or not target_port:
                    missing_fields = []
                    if not listen_port:
                        missing_fields.append("Listening Port")
                    if not target_ip:
                        missing_fields.append("Target IP")
                    if not target_port:
                        missing_fields.append("Target Port")

                    missing_message = f"Please provide valid values for: {', '.join(missing_fields)}."
                    if self.show_system_messages.get():
                        self.display_message(missing_message, sender="System")
                    return

                try:
                    ipaddress.ip_address(target_ip)
                except ValueError:
                    if self.show_system_messages.get():
                        self.display_message(f"Invalid IP address: {target_ip}. Please provide a valid IPv4 address.", sender="System")
                    return

                try:
                    listen_port = int(listen_port)
                    target_port = int(target_port)

                    if not (1 <= listen_port <= 65535):
                        if self.show_system_messages.get():
                            self.display_message(f"Listening port must be between 1 and 65535. Provided: {listen_port}.", sender="System")
                        return

                    if not (1 <= target_port <= 65535):
                        if self.show_system_messages.get():
                            self.display_message(f"Target port must be between 1 and 65535. Provided: {target_port}.", sender="System")
                        return

                except ValueError:
                    if self.show_system_messages.get():
                        self.display_message("Ports must be valid integers in the range 1-65535.", sender="System")
                    return

                self.node = P2PNode(listen_port, target_ip, target_port, self, ERROR_CHANCE, logger=self.logger)
                self.node.run()
                self.connection_status = f"Connected: Listening on {listen_port}, Targeting {target_ip}:{target_port}"
                self.update_status()
                self.logger.normal(f"Connection established on {listen_port}, targeting {target_ip}:{target_port}")
                if self.show_system_messages.get():
                    self.display_message(f"Connection established on {listen_port}, targeting {target_ip}:{target_port}", sender="System")
                self.connect_button.config(text="Disconnect")  
                self.update_elapsed_time_display()

            except Exception as e:
                if self.show_system_messages.get():
                    self.display_message(f"An error occurred: {str(e)}", sender="System")

        else:
            if self.node.running:
                self.node.send_disconnect_notification() 
            self.node.stop()
            self.node = None
            self.connection_status = "Disconnected"
            self.update_status()
            self.logger.normal(f"Connection terminated naturally.")
            if self.show_system_messages.get():
                self.display_message("Connection terminated.", sender="System")
            self.connect_button.config(text="Start Connection")  
            self.handshake_complete = False  
            self.update_handshake_progress(0)  
            self.stop_elapsed_time_display()  

    def toggle_connection_by_timeout(self):
            if self.node is not None and self.node.running:
                self.node.stop()
                self.node = None
                self.connection_status = "Disconnected (Timeout)"
                self.update_status()
                self.logger.normal(f"Connection terminated due to heartbeat timeout.")
                if self.show_system_messages.get():
                    self.display_message("Connection terminated due to heartbeat timeout.", sender="System")
                self.connect_button.config(text="Start Connection")  
                self.handshake_complete = False 
                self.update_handshake_progress(0) 
                self.stop_elapsed_time_display()  

    def open_options_window(self):
            options_window = tk.Toplevel(self.root)
            options_window.title("Options")
            options_window.geometry("600x650")
            options_window.configure(bg="#e0e0e0")  

            content_frame = tk.Frame(options_window, bg="#e0e0e0")
            content_frame.pack(expand=True, fill=tk.BOTH)

            left_nav_frame = tk.Frame(content_frame, bg="#e0e0e0", width=50)
            left_nav_frame.pack(side=tk.LEFT, fill=tk.Y)

            center_frame = tk.Frame(content_frame, bg="#e0e0e0")
            center_frame.pack(side=tk.LEFT, expand=True, fill=tk.BOTH)

            right_nav_frame = tk.Frame(content_frame, bg="#e0e0e0", width=50)
            right_nav_frame.pack(side=tk.RIGHT, fill=tk.Y)

            self.page_frame = tk.Frame(center_frame, bg="#e0e0e0")
            self.page_frame.pack(expand=True, fill=tk.BOTH)

            self.page1 = tk.Frame(self.page_frame, bg="#e0e0e0")
            self.page1.pack(expand=True, fill=tk.BOTH)

            theme_frame = tk.Frame(self.page1, bg="#ffffff", bd=1, relief="solid")
            theme_frame.pack(pady=10, padx=20, fill=tk.X)

            theme_title = tk.Label(theme_frame, text="Theme Settings", font=("Arial", 14, "bold"), bg="#2196f3", fg="white", anchor="w", padx=10)
            theme_title.pack(fill=tk.X)

            theme_inner_frame = tk.Frame(theme_frame, bg="#ffffff")
            theme_inner_frame.pack(pady=10, padx=10)

            themes = [("Light", "light"), ("Dark", "dark"), ("Blue", "blue"), ("Green", "green"), ("Purple", "purple")]

            self.theme_colors = {
                "light": "#e9ecef",
                "dark": "#333333",
                "blue": "#007bff",
                "green": "#28a745",
                "purple": "#6f42c1"
            }

            self.style = ttk.Style()
            self.style.configure('TButton', font=("Arial", 10))  

            for idx, (text, value) in enumerate(themes):
                radio = ttk.Radiobutton(theme_inner_frame, text=text, variable=self.theme_var, value=value, command=self.update_theme)
                radio.grid(row=0, column=idx+1, padx=10)

                color_frame = tk.Frame(theme_inner_frame, bg="#ffffff")
                color_frame.grid(row=1, column=idx+1, pady=(0, 10))

                self.create_color_circle(color_frame, self.theme_colors[value])

            font_size_frame = tk.Frame(self.page1, bg="#ffffff", bd=1, relief="solid")
            font_size_frame.pack(pady=10, padx=20, fill=tk.X)

            font_size_title = tk.Label(font_size_frame, text="Font Settings", font=("Arial", 14, "bold"), bg="#607d8b", fg="white", anchor="w", padx=10)
            font_size_title.pack(fill=tk.X)

            font_size_inner_frame = tk.Frame(font_size_frame, bg="#ffffff")
            font_size_inner_frame.pack(pady=10, padx=10)

            font_size_label = tk.Label(font_size_inner_frame, text="Font Size:", font=("Arial", 12), bg="#ffffff", anchor="w")
            font_size_label.grid(row=0, column=0, padx=10, pady=10, sticky="w")

            font_sizes = ["Very Small", "Small", "Normal", "Large", "Very Large"]

            font_size_dropdown = ttk.Combobox(font_size_inner_frame, textvariable=self.selected_font_size, values=font_sizes, state="readonly", font=("Arial", 10))
            font_size_dropdown.grid(row=0, column=1, padx=10, pady=10)

            font_family_label = tk.Label(font_size_inner_frame, text="Font Family:", font=("Arial", 12), bg="#ffffff", anchor="w")
            font_family_label.grid(row=1, column=0, padx=10, pady=10, sticky="w")

            font_families = [
                "Arial", "Courier", "Helvetica", "Times New Roman", "Verdana",  # Basic Fonts
                "Georgia", "Palatino", "Garamond", "Baskerville", "Book Antiqua",  # Serif Fonts
                "Calibri", "Arial Narrow", "Futura", "Roboto", "Lato",  # Sans-serif Fonts
                "Consolas", "Courier New", "Lucida Console", "Inconsolata",  # Monospace Fonts
                "Impact", "Comic Sans MS", "Rockwell", "Bodoni", "Playbill",  # Display Fonts
                "Brush Script", "Pacifico", "Dancing Script", "Lobster", "Fredericka the Great",  # Handwritten Fonts
                "Copperplate Gothic", "American Typewriter", "Zapfino", "Futura Condensed",  # Retro & Vintage Fonts
                "Tahoma", "Geneva", "Trebuchet MS",  # Web-safe Fonts
                "Papyrus", "Old English Text", "Algerian"  # Fantasy Fonts
            ]

            font_family_dropdown = ttk.Combobox(font_size_inner_frame, textvariable=self.selected_font_family, values=font_families, state="readonly", font=("Arial", 10))
            font_family_dropdown.grid(row=1, column=1, padx=10, pady=10)
            
            self.selected_font_family.trace_add("write", self.update_font_family)
            self.selected_font_size.trace_add("write", self.update_font_size)
 
            sound_frame = tk.Frame(self.page1, bg="#ffffff", bd=1, relief="solid")
            sound_frame.pack(pady=10, padx=20, fill=tk.X)

            sound_title = tk.Label(sound_frame, text="Notification Settings", font=("Arial", 14, "bold"), bg="#3f51b5", fg="white", anchor="w", padx=10)
            sound_title.pack(fill=tk.X)

            sound_inner_frame = tk.Frame(sound_frame, bg="#ffffff")
            sound_inner_frame.pack(pady=10, padx=10)

            notification_sounds = [
                ("None", "none"),  
                ("Angry", os.path.join(self.base_path, "zvuky", "notification.wav") 
                        if os.path.isfile(os.path.join(self.base_path, "zvuky", "notification.wav")) else "none"),
                ("Windows", os.path.join(self.base_path, "zvuky", "notification1.wav") 
                        if os.path.isfile(os.path.join(self.base_path, "zvuky", "notification1.wav")) else "none"),
                ("Sci-fi", os.path.join(self.base_path, "zvuky", "notification2.wav") 
                        if os.path.isfile(os.path.join(self.base_path, "zvuky", "notification2.wav")) else "none"),
                ("Pop", os.path.join(self.base_path, "zvuky", "notification3.wav") 
                    if os.path.isfile(os.path.join(self.base_path, "zvuky", "notification3.wav")) else "none"),
                ("Echo", os.path.join(self.base_path, "zvuky", "notification4.wav") 
                        if os.path.isfile(os.path.join(self.base_path, "zvuky", "notification4.wav")) else "none")
            ]
            for idx, (text, value) in enumerate(notification_sounds):
                row = idx // 3  
                column = idx % 3  

                radio = ttk.Radiobutton(sound_inner_frame, text=text, variable=self.selected_sound_var, value=value)
                radio.grid(row=row, column=column, padx=10, pady=5)  

            test_sound_button = tk.Button(sound_inner_frame, text="Test Selected Sound", font=("Arial", 10), bg="#4caf50", fg="white", command=self.play_sound)
            test_sound_button.grid(row=2, columnspan=len(notification_sounds), pady=5)

            checkbox_inner_frame = tk.Frame(sound_frame, bg="#ffffff")
            checkbox_inner_frame.pack(pady=10, padx=10)

            self.show_notification_desktop = ttk.Checkbutton(checkbox_inner_frame, text="Show Desktop Notifications", variable=self.show_desktop_notification, style="TCheckbutton")
            self.show_notification_desktop.grid(row=0, column=0, sticky="w", padx=10, pady=5)

            self.page2 = tk.Frame(self.page_frame, bg="#e0e0e0")

            self.style = ttk.Style()
            
            self.style.configure('TCheckbutton',
                                font=("Arial", 10),
                                padding=6)
            
            display_frame = tk.Frame(self.page2, bg="#ffffff", bd=1, relief="solid")
            display_frame.pack(pady=10, padx=20, fill=tk.X)

            display_title = tk.Label(display_frame, text="Messages", font=("Arial", 14, "bold"), bg="#4caf50", fg="white", anchor="w", padx=10)
            display_title.pack(fill=tk.X)

            checkbox_inner_frame = tk.Frame(display_frame, bg="#ffffff")
            checkbox_inner_frame.pack(pady=10, padx=10)

            self.sent_heartbeat_checkbox = ttk.Checkbutton(checkbox_inner_frame, text="Show Sent Heartbeats", variable=self.show_sent_heartbeat, style='TCheckbutton')
            self.sent_heartbeat_checkbox.grid(row=0, column=0, sticky="w", padx=10, pady=5)

            self.received_heartbeat_checkbox = ttk.Checkbutton(checkbox_inner_frame, text="Show Received Heartbeats", variable=self.show_received_heartbeat, style='TCheckbutton')
            self.received_heartbeat_checkbox.grid(row=1, column=0, sticky="w", padx=10, pady=5)

            self.show_sent_fragments_checkbox = ttk.Checkbutton(checkbox_inner_frame, text="Show Sent Fragments ⚠️", variable=self.show_sent_fragments, style='TCheckbutton')
            self.show_sent_fragments_checkbox.grid(row=2, column=0, sticky="w", padx=10, pady=5)

            self.show_sent_fragments_checkbox.bind("<Enter>", self.show_sent_fragments_tooltip)
            self.show_sent_fragments_checkbox.bind("<Leave>", self.hide_sent_fragments_tooltip)  

            self.show_system_messages_checkbox = ttk.Checkbutton(checkbox_inner_frame, text="Show System Messages ⚠️", variable=self.show_system_messages, style='TCheckbutton')
            self.show_system_messages_checkbox.grid(row=3, column=1, sticky="w", padx=10, pady=5)

            self.show_system_messages_checkbox.bind("<Enter>", self.show_sent_fragments_tooltip)  
            self.show_system_messages_checkbox.bind("<Leave>", self.hide_sent_fragments_tooltip)  

            self.show_ack_nack_checkbox = ttk.Checkbutton(checkbox_inner_frame, text="Show ACK/NACK Messages ⚠️", variable=self.show_ack_nack, style='TCheckbutton')
            self.show_ack_nack_checkbox.grid(row=3, column=0, sticky="w", padx=10, pady=5)

            self.show_ack_nack_checkbox.bind("<Enter>", self.show_sent_fragments_tooltip)  
            self.show_ack_nack_checkbox.bind("<Leave>", self.hide_sent_fragments_tooltip)  

            self.show_ip_checkbox = ttk.Checkbutton(checkbox_inner_frame, text="Show My IP in Chat", variable=self.show_ip_address, style='TCheckbutton')
            self.show_ip_checkbox.grid(row=0, column=1, sticky="w", padx=10, pady=5)

            self.show_total_fragments_checkbox = ttk.Checkbutton(checkbox_inner_frame, text="Show Total Fragments", variable=self.show_total_fragments, style='TCheckbutton')
            self.show_total_fragments_checkbox.grid(row=1, column=1, sticky="w", padx=10, pady=5)

            self.show_elapsed_time_checkbox = ttk.Checkbutton(checkbox_inner_frame, text="Show Elapsed Time", variable=self.show_elapsed_time, style='TCheckbutton')
            self.show_elapsed_time_checkbox.grid(row=2, column=1, sticky="w", padx=10, pady=5)

            delete_frame = tk.Frame(self.page2, bg="#ffffff", bd=1, relief="solid")
            delete_frame.pack(pady=10, padx=20, fill=tk.X)

            delete_title = tk.Label(delete_frame, text="Message Operations", font=("Arial", 14, "bold"), bg="#f44336", fg="white", anchor="w", padx=10)
            delete_title.pack(fill=tk.X)

            delete_inner_frame = tk.Frame(delete_frame, bg="#ffffff")
            delete_inner_frame.pack(pady=10, padx=10)

            delete_button = tk.Button(delete_inner_frame, text="Delete All Chat Messages", font=("Arial", 10), bg="#f44336", fg="white", command=self.delete_all_messages)
            delete_button.grid(row=0, column=0, padx=(0, 10), pady=5, sticky="ew")  

            delete_heartbeat_button = tk.Button(delete_inner_frame, text="Delete All Heartbeat Messages", font=("Arial", 10), bg="#f44336", fg="white", command=self.delete_all_heartbeat_messages)
            delete_heartbeat_button.grid(row=0, column=1, pady=5, sticky="ew")  

            export_button = tk.Button(delete_inner_frame, text="Export Chat History", font=("Arial", 10), bg="#4caf50", fg="white", command=self.export_chat_history)
            export_button.grid(row=1, column=0, padx=(0, 10), pady=10, sticky="ew") 

            view_log_button = tk.Button(delete_inner_frame, text="View Developer Log", font=("Arial", 10), bg="#2196f3", fg="white", command=self.view_printf_log)
            view_log_button.grid(row=1, column=1, pady=10, sticky="ew")  

            buffer_size_frame = tk.Frame(self.page2, bg="#ffffff", bd=1, relief="solid")
            buffer_size_frame.pack(pady=10, padx=20, fill=tk.X)

            buffer_size_title = tk.Label(buffer_size_frame, text="Fragment Sizes", font=("Arial", 14, "bold"), bg="#ff9800", fg="white", anchor="w", padx=10)
            buffer_size_title.pack(fill=tk.X)

            buffer_size_inner_frame = tk.Frame(buffer_size_frame, bg="#ffffff")
            buffer_size_inner_frame.pack(pady=10, padx=10)

            buffer_size_label = tk.Label(buffer_size_inner_frame, text="Max Fragment Size (Bytes):", font=("Arial", 10), bg="#ffffff")
            buffer_size_label.grid(row=0, column=0, sticky=tk.W)

            self.buffer_size_entry = tk.Entry(buffer_size_inner_frame, font=("Arial", 10), bg="#f0f0f0", bd=1, relief="solid")
            self.buffer_size_entry.grid(row=0, column=1, padx=(5, 0))
            self.buffer_size_entry.insert(0, str(BUFFER_SIZE))  

            file_buffer_size_label = tk.Label(buffer_size_inner_frame, text="Max File Buffer Size (Bytes):", font=("Arial", 10), bg="#ffffff")
            file_buffer_size_label.grid(row=1, column=0, sticky=tk.W)

            self.file_buffer_size_entry = tk.Entry(buffer_size_inner_frame, font=("Arial", 10), bg="#f0f0f0", bd=1, relief="solid")
            self.file_buffer_size_entry.grid(row=1, column=1, padx=(5, 0))
            self.file_buffer_size_entry.insert(0, str(FILE_BUFFER_SIZE)) 

            error_chance_frame = tk.Frame(self.page2, bg="#ffffff", bd=1, relief="solid")
            error_chance_frame.pack(pady=10, padx=20, fill=tk.X)

            self.error_chance_title = tk.Label(error_chance_frame, text="Error Chance Settings ⚠️", font=("Arial", 14, "bold"), bg="#ff5722", fg="white", anchor="w", padx=10)
            self.error_chance_title.pack(fill=tk.X)

            self.error_chance_title.bind("<Enter>", self.show_error_chance_tooltip)  
            self.error_chance_title.bind("<Leave>", self.hide_error_chance_tooltip)  

            error_chance_inner_frame = tk.Frame(error_chance_frame, bg="#ffffff")
            error_chance_inner_frame.pack(pady=10, padx=10)

            error_chance_label = tk.Label(error_chance_inner_frame, text="Set Error Chance (%):", font=("Arial", 10), bg="#ffffff")
            error_chance_label.grid(row=0, column=0, sticky=tk.W)

            self.error_chance_entry = tk.Entry(error_chance_inner_frame, font=("Arial", 10), bg="#f0f0f0", bd=1, relief="solid")
            self.error_chance_entry.grid(row=0, column=1, padx=(5, 0))
            self.error_chance_entry.insert(0, str(ERROR_CHANCE))  

            left_nav_button = tk.Button(
                left_nav_frame, 
                text="<", 
                font=("Arial", 12), 
                bg="#007bff",  
                fg="white",  
                relief="raised", 
                command=lambda: self.show_page1() if self.page2.winfo_ismapped() else self.show_page2()
            )
            left_nav_button.place(relx=0.5, rely=0.5, anchor=tk.CENTER)  

            right_nav_button = tk.Button(
                right_nav_frame, 
                text=">", 
                font=("Arial", 12), 
                bg="#007bff", 
                fg="white",  
                relief="raised",  
                command=lambda: self.show_page2() if self.page1.winfo_ismapped() else self.show_page1()
            )
            right_nav_button.place(relx=0.5, rely=0.5, anchor=tk.CENTER)  


            def validate_and_close():
                MIN_SIZE = 1 
                MAX_SIZE = 1472  

                global ERROR_CHANCE

                self.previous_error_chance = ERROR_CHANCE  


                try:
                    global BUFFER_SIZE
                    BUFFER_SIZE = int(self.buffer_size_entry.get())
                    if not (MIN_SIZE <= BUFFER_SIZE <= MAX_SIZE):
                        raise ValueError(f"Buffer size must be between {MIN_SIZE} and {MAX_SIZE} bytes.")
                    
                    global FILE_BUFFER_SIZE
                    FILE_BUFFER_SIZE = int(self.file_buffer_size_entry.get())
                    if not (MIN_SIZE <= FILE_BUFFER_SIZE <= MAX_SIZE):
                        raise ValueError(f"File buffer size must be between {MIN_SIZE} and {MAX_SIZE} bytes.")
                    
                    error_chance = float(self.error_chance_entry.get())  
                    
                    if not (0 <= error_chance <= 100):  
                        raise ValueError("Error chance must be between 0 and 100.")
                    
                    ERROR_CHANCE = error_chance  
                    if error_chance != self.previous_error_chance:
                        if self.node is not None:
                            self.node.notify_error_chance_update(error_chance)
                        else:
                            pass  

                    self.update_status() 
                    options_window.destroy()  
                
                except ValueError as e:
                    if self.show_system_messages.get():
                        self.display_message(f"Error. Options not applied! {e}", sender="System")

            button_frame = tk.Frame(options_window, bg="#e0e0e0")
            button_frame.pack(pady=5)

            close_button = tk.Button(button_frame, text="Save", font=("Arial", 12, "bold"), command=validate_and_close, bg="#009688", fg="white", width=10, relief="raised", bd=2)
            close_button.pack()

            options_window.bind("<Return>", lambda event: validate_and_close())

            self.show_page2()

    def show_page1(self):
        self.page1.pack(expand=True, fill=tk.BOTH)
        self.page2.pack_forget()  

    def show_page2(self):
        self.page2.pack(expand=True, fill=tk.BOTH)
        self.page1.pack_forget()  


    def create_color_circle(self, frame, color):
        circle = tk.Canvas(frame, width=30, height=30, bg="#ffffff", highlightthickness=0)  
        circle.create_oval(5, 5, 27, 27, fill=color, outline=color)  
        circle.pack(side=tk.TOP, pady=(0, 5))  

    def update_font_size(self, *args):
        font_size_mapping = {
            "Very Small": 8,
            "Small": 10,
            "Normal": 12,
            "Large": 14,
            "Very Large": 16
        }

        selected_size = font_size_mapping[self.selected_font_size.get()]

        current_family = self.selected_font_family.get()

        self.chat_area.tag_configure("user_message", font=(current_family, selected_size, "normal"))
        self.chat_area.tag_configure("other_message", font=(current_family, selected_size, "normal"))
        self.chat_area.tag_configure("system_message", font=(current_family, selected_size, "normal"))
        self.chat_area.tag_configure("message_time", font=(current_family, selected_size - 2, "italic"))
        self.chat_area.tag_configure("message_time_other", font=(current_family, selected_size - 2, "italic"))
        self.chat_area.tag_configure("message_time_system", font=(current_family, selected_size - 2, "italic"))

    def update_font_family(self, *args):
        selected_family = self.selected_font_family.get()
        current_size = self.selected_font_size.get()

        font_size_mapping = {
            "Very Small": 8,
            "Small": 10,
            "Normal": 12,
            "Large": 14,
            "Very Large": 16
        }

        selected_size = font_size_mapping[current_size]

        self.chat_area.tag_configure("user_message", font=(selected_family, selected_size, "normal"))
        self.chat_area.tag_configure("other_message", font=(selected_family, selected_size, "normal"))
        self.chat_area.tag_configure("system_message", font=(selected_family, selected_size, "normal"))
        self.chat_area.tag_configure("message_time", font=(selected_family, selected_size - 2, "italic"))
        self.chat_area.tag_configure("message_time_other", font=(selected_family, selected_size - 2, "italic"))
        self.chat_area.tag_configure("message_time_system", font=(selected_family, selected_size - 2, "italic"))
        

    def delete_all_messages(self):
        self.chat_area.configure(state='normal')
        self.chat_area.delete(1.0, tk.END)
        
        self.chat_area.configure(state='disabled')
        
        if self.show_system_messages.get():  
            self.display_message("Chat history cleared.", sender="System") 

    def delete_all_heartbeat_messages(self):

        self.heartbeat_area.configure(state='normal')
        self.heartbeat_area.delete(1.0, tk.END)
        
        self.heartbeat_area.configure(state='disabled')
        
        if self.show_system_messages.get():  
            self.display_message("Chat history cleared.", sender="System")  
        
    def view_printf_log(self):
        if os.path.exists(self.log_file_path):
            with open(self.log_file_path, 'r', encoding='utf-8') as log_file:
                log_content = log_file.read()
            self.show_log_in_textbox(log_content)

    def show_log_in_textbox(self, log_content):
        log_window = tk.Toplevel(self.root)
        log_window.title("Printf Log")
        
        frame = tk.Frame(log_window)
        frame.pack(padx=10, pady=10) 

        text_widget = tk.Text(frame, wrap='word', width=80, height=20)

        scrollbar = tk.Scrollbar(frame, orient="vertical", command=text_widget.yview)
        text_widget.config(yscrollcommand=scrollbar.set)

        text_widget.pack(side=tk.LEFT, fill=tk.BOTH, expand=True)
        scrollbar.pack(side=tk.RIGHT, fill=tk.Y)
        
        text_widget.tag_configure("NORMAL", foreground="gray")
        text_widget.tag_configure("SEND", foreground="green")
        text_widget.tag_configure("RECEIVE", foreground="blue")
        text_widget.tag_configure("TIMEOUT", foreground="orange")
        text_widget.tag_configure("ERROR", foreground="red")

        def insert_log_with_color(log_content, filter_level=None):
            lines = log_content.splitlines()
            for line in lines:
                if filter_level and filter_level not in line:
                    continue 

                if "NORMAL" in line:
                    text_widget.insert('end', line + '\n', "NORMAL")
                elif "SEND" in line:
                    text_widget.insert('end', line + '\n', "SEND")
                elif "RECEIVE" in line:
                    text_widget.insert('end', line + '\n', "RECEIVE")
                elif "TIMEOUT" in line:
                    text_widget.insert('end', line + '\n', "TIMEOUT")
                elif "ERROR" in line:
                    text_widget.insert('end', line + '\n', "ERROR")
                else:
                    text_widget.insert('end', line + '\n')  

        insert_log_with_color(log_content)

        text_widget.config(state=tk.DISABLED)  

        def enable_editing_and_clear():
            text_widget.config(state=tk.NORMAL)  
            text_widget.delete(1.0, tk.END)  
            with open(self.log_file_path, 'w', encoding='utf-8') as log_file:
                log_file.truncate(0)  
            text_widget.config(state=tk.DISABLED) 

        search_filter_frame = tk.Frame(log_window)
        search_filter_frame.pack(padx=10, pady=10, fill=tk.X)

        search_frame = tk.Frame(search_filter_frame)
        search_frame.pack(side=tk.TOP, pady=5, anchor="center")

        search_label = tk.Label(search_frame, text="Search:")
        search_label.pack(side=tk.LEFT)

        search_entry = tk.Entry(search_frame, width=20)
        search_entry.pack(side=tk.LEFT)

        def search_log():
            search_term = search_entry.get().lower()
            
            if search_term:
                text_widget.config(state=tk.NORMAL)  
                text_widget.delete(1.0, tk.END)  
                insert_log_with_color(log_content)  
                text_widget.mark_set("match_start", "1.0")
                
                while True:
                    match_start = text_widget.search(search_term, "match_start", stopindex=tk.END, nocase=True)  
                    if not match_start:
                        break
                    match_end = f"{match_start}+{len(search_term)}c"
                    text_widget.tag_add("highlight", match_start, match_end)
                    text_widget.mark_set("match_start", match_end)

                text_widget.tag_configure("highlight", background="yellow", foreground="black")
                text_widget.config(state=tk.DISABLED)  


        search_button = tk.Button(search_frame, text="Search", command=search_log, bg="#4CAF50", fg="white", relief="raised", bd=2)
        search_button.pack(side=tk.LEFT, padx=10)

        filter_frame = tk.Frame(search_filter_frame)
        filter_frame.pack(side=tk.TOP, pady=5, anchor="center")

        filter_label = tk.Label(filter_frame, text="Filter by Level:")
        filter_label.pack(side=tk.LEFT)

        filter_options = ["All", "NORMAL", "SEND", "RECEIVE", "TIMEOUT", "ERROR"]
        filter_var = tk.StringVar(value="All")  
        filter_menu = ttk.Combobox(filter_frame, textvariable=filter_var, values=filter_options, state="readonly")
        filter_menu.pack(side=tk.LEFT)

        def filter_log_by_level():
            selected_level = filter_var.get()  
            text_widget.config(state=tk.NORMAL)  
            text_widget.delete(1.0, tk.END)  
            insert_log_with_color(log_content, filter_level=selected_level if selected_level != "All" else None)
            text_widget.config(state=tk.DISABLED)

        filter_button = tk.Button(filter_frame, text="Apply Filter", command=filter_log_by_level, bg="#4CAF50", fg="white", relief="raised", bd=2)
        filter_button.pack(side=tk.LEFT, padx=10)

        button_frame = tk.Frame(log_window)
        button_frame.pack(side=tk.BOTTOM, pady=10)

        delete_content_button = tk.Button(button_frame, text="Delete Log Content", font=("Arial", 12, "bold"), bg="#f44336", fg="white", command=enable_editing_and_clear, relief="raised", bd=2, width=20)
        delete_content_button.pack(side=tk.LEFT, padx=20)

        close_button = tk.Button(button_frame, text="Close", font=("Arial", 12, "bold"), command=log_window.destroy, bg="#009688", fg="white", relief="raised", bd=2, width=10)
        close_button.pack(side=tk.LEFT, padx=20)

    def update_theme(self):
            if self.theme_var.get() == "dark":
                self.root.configure(bg="#333333")
                self.chat_area.configure(bg="#444444", fg="white")
                self.heartbeat_area.configure(bg="#444444", fg="white")
            elif self.theme_var.get() == "blue":
                self.root.configure(bg="#007bff") 
                self.chat_area.configure(bg="#cce5ff", fg="black")  
                self.heartbeat_area.configure(bg="#cce5ff", fg="black")  
            elif self.theme_var.get() == "green":
                self.root.configure(bg="#28a745")  
                self.chat_area.configure(bg="#d4edda", fg="black")  
                self.heartbeat_area.configure(bg="#d4edda", fg="black")  
            elif self.theme_var.get() == "purple":
                self.root.configure(bg="#6f42c1")  
                self.chat_area.configure(bg="#e2d8f3", fg="black")  
                self.heartbeat_area.configure(bg="#e2d8f3", fg="black")  
            else:  
                self.root.configure(bg="#e9ecef")
                self.chat_area.configure(bg="#f8f9fa", fg="black")
                self.heartbeat_area.configure(bg="#ffffff", fg="black")

    def export_chat_history(self):
        
        default_filename = datetime.now().strftime("%Y-%m-%d_%H-%M-%S") + ".txt"
        
        file_path = filedialog.asksaveasfilename(defaultextension=".txt", 
                                                 initialfile=default_filename,  
                                                 filetypes=[("Text files", "*.txt"), ("All files", "*.*")])
        if file_path:

            with open(file_path, 'w', encoding='utf-8') as file:

                chat_content = self.chat_area.get("1.0", tk.END).strip()  
                
                chat_lines = chat_content.splitlines()

                for line in chat_lines:
                    if line.strip():  
                        timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")  
                        formatted_line = f"[{timestamp}]: {line}\n"
                        file.write(formatted_line)
            
            if self.show_system_messages.get():
                self.display_message(f"Chat history exported to {file_path}", sender="System")

    def open_help_window(self):
        help_window = tk.Toplevel(self.root)
        help_window.title("Help")
        help_window.geometry("650x800")  
        help_window.configure(bg="#e0e0e0")  

        shortcuts_frame = tk.Frame(help_window, bg="#ffffff", bd=2, relief="groove")
        shortcuts_frame.pack(pady=10, padx=20, fill=tk.BOTH, expand=True)

        shortcuts_title = tk.Label(shortcuts_frame, text="Keyboard Shortcuts", font=("Arial", 16, "bold"), bg="#2196f3", fg="white", anchor="w", padx=10)
        shortcuts_title.pack(fill=tk.X)

        shortcuts_list = [
            ("🅂", "S: Start Connection"),
            ("🄾", "O: Open Options"),
            ("🄴", "E: Exit Application"),
            ("🄷", "H: Help"),
            ("🄵", "F: Toggle Fullscreen"),
            ("⏎", "Enter: Confirm")
        ]

        for emoji, shortcut in shortcuts_list:

            line_frame = tk.Frame(shortcuts_frame, bg="#ffffff")
            line_frame.pack(pady=(5, 0), padx=10, fill=tk.X)

            emoji_label = tk.Label(line_frame, text=emoji, font=("Arial", 16), bg="#ffffff")
            emoji_label.pack(side=tk.LEFT)

            data_label = tk.Label(line_frame, text=shortcut.split(":")[0] + ":", font=("Arial", 14), bg="#ffffff")
            data_label.pack(side=tk.LEFT, padx=(30, 0))  
            
            value_label = tk.Label(line_frame, text=shortcut.split(":")[1].strip(), font=("Arial", 12), bg="#ffffff")
            value_label.pack(side=tk.LEFT, padx=(5, 0))  

        shortcuts_separator = tk.Frame(shortcuts_frame, bg="#2196f3", height=2)
        shortcuts_separator.pack(fill=tk.X, padx=10)

        usage_frame = tk.Frame(help_window, bg="#ffffff", bd=2, relief="groove")
        usage_frame.pack(pady=10, padx=20, fill=tk.BOTH, expand=True)

        usage_title = tk.Label(usage_frame, text="Commom Problems", font=("Arial", 16, "bold"), bg="#ff9800", fg="white", anchor="w", padx=10)
        usage_title.pack(fill=tk.X)

        usage_instructions = [
            ("⓵", "PORTS: Make sure the ports are correct."),
            ("⓶", "NETWORK: Ensure the other node is in the same network."),
            ("⓷", "CONNECTION: Check your connection status.")
        ]

        for emoji, instruction in usage_instructions:
            line_frame = tk.Frame(usage_frame, bg="#ffffff")
            line_frame.pack(pady=(5, 0), padx=10, fill=tk.X)

            emoji_label = tk.Label(line_frame, text=emoji, font=("Arial", 16), bg="#ffffff")
            emoji_label.pack(side=tk.LEFT)

            instruction_label = tk.Label(line_frame, text=instruction, font=("Arial", 14), bg="#ffffff")
            instruction_label.pack(side=tk.LEFT, padx=(30, 0)) 

        usage_separator = tk.Frame(usage_frame, bg="#ff9800", height=2)
        usage_separator.pack(fill=tk.X, padx=10)

        author_frame = tk.Frame(help_window, bg="#ffffff", bd=2, relief="groove")
        author_frame.pack(pady=10, padx=20, fill=tk.BOTH, expand=True)

        author_title = tk.Label(author_frame, text="Author Information", font=("Arial", 16, "bold"), bg="#673ab7", fg="white", anchor="w", padx=10)
        author_title.pack(fill=tk.X)

        author_info = [
            ("👤", "Author: Ondrej Drengubiak    |    120932"),
            ("👨‍🏫", "Instructor: Ing. Lukáš Mastiľak"),
            ("🏫", "University: Slovak University of Technology in Bratislava"),
            ("🎓", "Faculty: Faculty of Informatics and Information Technologies (FIIT)"),
            ("📚", "Course: PKS - Computer and Communication Systems"),
            ("📞", "Contact: xdrengubiak@stuba.sk    |    Bratislava/Zvolen, Slovakia"),  
            ("🔧", "Version: 1.0.1 (Final release)"),
        ]

        for emoji, text in author_info:
            line_frame = tk.Frame(author_frame, bg="#ffffff")
            line_frame.pack(pady=(5, 0), padx=10, fill=tk.X)

            emoji_label = tk.Label(line_frame, text=emoji, font=("Arial", 16), bg="#ffffff")
            emoji_label.pack(side=tk.LEFT)

        
            data_label = tk.Label(line_frame, text=text.split(":")[0] + ":", font=("Arial", 14), bg="#ffffff")
            data_label.pack(side=tk.LEFT, padx=(30, 0))  

            value_label = tk.Label(line_frame, text=text.split(":")[1].strip(), font=("Arial", 12), bg="#ffffff")
            value_label.pack(side=tk.LEFT, padx=(5, 0)) 

            line_separator = tk.Frame(author_frame, bg="#673ab7", height=2)
            line_separator.pack(fill=tk.X, padx=10)

        button_frame = tk.Frame(help_window, bg="#e0e0e0")
        button_frame.pack(pady=10)

        close_help_button = tk.Button(button_frame, text="Close", font=("Arial", 12, "bold"), command=help_window.destroy, bg="#4caf50", fg="white", width=12, relief="raised", bd=2)
        close_help_button.pack()

        help_window.bind("<Return>", lambda event: help_window.destroy())

    def play_sound(self):
        sound_file = self.selected_sound_var.get()  
        if sound_file != 'none':
                winsound.PlaySound(sound_file, winsound.SND_FILENAME)
        
    def toggle_fullscreen(self):
        """ Toggle fullscreen mode for the chat window """
        if self.root.attributes('-fullscreen'):
            self.root.attributes('-fullscreen', False)  
            self.root.geometry("700x700")  
            self.fullscreen_button.config(text="🗖") 
            self.heartbeat_frame.pack(side=tk.LEFT, fill=tk.Y, padx=10, pady=10)  
        else:
            self.root.attributes('-fullscreen', True) 
            self.fullscreen_button.config(text="🗕") 
            self.heartbeat_frame.pack_forget()  

    def exit_application(self):
        """ Stop the node and exit the application """
        if self.node:
            self.node.stop()
        self.on_close()
        self.root.quit()

    def start_fade_in_animation(self):
        alpha = 0

        def fade_in():
            nonlocal alpha
            if alpha < 1:
                alpha += 0.05  
                self.root.attributes("-alpha", alpha)
                self.root.after(50, fade_in)  
            else:
                self.root.attributes("-alpha", 1)  

        fade_in()

    def show_splash_screen(self):
        self.canvas = tk.Canvas(self.root, width=760, height=700, bg="#e9ecef", bd=0, highlightthickness=0)
        self.canvas.pack()

        logo_path = os.path.join(os.path.dirname(__file__), 'profilovky', 'icon.png') 
        try:
            self.img = Image.open(logo_path)
            self.img = self.img.convert("RGBA")  
            self.original_image = self.img.copy()  
            self.logo_image = ImageTk.PhotoImage(self.img)
            self.logo = self.canvas.create_image(380, 350, image=self.logo_image)
        except Exception as e:
            self.logger.error(f"Error loading image: {e}")

        self.fade_out_logo()

    def fade_out_logo(self, alpha=255, step=5):
        if alpha <= 0:
            self.canvas.destroy()
        else:
            img = self.original_image.copy()
            alpha_channel = img.split()[3]  
            alpha_channel = alpha_channel.point(lambda p: p * alpha / 255) 
            img.putalpha(alpha_channel) 

            self.logo_image = ImageTk.PhotoImage(img)

            self.canvas.itemconfig(self.logo, image=self.logo_image)

            self.canvas.after(50, self.fade_out_logo, alpha - step, step)

    def show_sent_fragments_tooltip(self, event):
        tooltip_text = "Excesive displays may impact performance!"

        self.sent_fragments_tooltip = tk.Toplevel(self.root)
        self.sent_fragments_tooltip.wm_overrideredirect(True)  
        self.sent_fragments_tooltip.config(bg="#ffffe0")  
        
        tooltip_label = tk.Label(self.sent_fragments_tooltip, text=tooltip_text, font=("Arial", 10, "italic"), fg="gray", bg="#ffffe0", bd=1, relief="solid")
        tooltip_label.pack(padx=5, pady=5)

        x = event.x_root + 10  
        y = event.y_root + 10  
        
        self.sent_fragments_tooltip.geometry(f"+{x}+{y}")
        
        self.sent_fragments_tooltip.lift() 
        self.sent_fragments_tooltip.attributes("-topmost", 1)  

    def hide_sent_fragments_tooltip(self, event):
        if hasattr(self, 'sent_fragments_tooltip'):
            self.sent_fragments_tooltip.destroy()
            del self.sent_fragments_tooltip

    def show_error_chance_tooltip(self, event):
        tooltip_text = "Higher values (over 3%) may slow sending! Best at 0 or 1."

        self.error_chance_tooltip = tk.Toplevel(self.root)
        self.error_chance_tooltip.wm_overrideredirect(True)  
        self.error_chance_tooltip.config(bg="#ffffe0")  
        
        tooltip_label = tk.Label(self.error_chance_tooltip, text=tooltip_text, font=("Arial", 10, "italic"), fg="gray", bg="#ffffe0", bd=1, relief="solid")
        tooltip_label.pack(padx=5, pady=5)

        x = event.x_root + 10  
        y = event.y_root + 10  
        
        self.error_chance_tooltip.geometry(f"+{x}+{y}")
        
        self.error_chance_tooltip.lift() 
        self.error_chance_tooltip.attributes("-topmost", 1)  

    def hide_error_chance_tooltip(self, event):
        if hasattr(self, 'error_chance_tooltip'):
            self.error_chance_tooltip.destroy()
            del self.error_chance_tooltip

    def set_taskbar_icon(self):
        icon_path_ico = os.path.join(os.path.dirname(__file__), 'profilovky', 'icon.ico')
        icon_path_png = os.path.join(os.path.dirname(__file__), 'profilovky', 'icon.png')
        
        if platform.system() == "Windows":
            if os.path.exists(icon_path_ico):
                self.root.wm_iconbitmap(icon_path_ico)
        elif platform.system() == "Darwin":  # macOS
            pass
        elif platform.system() == "Linux":
            if os.path.exists(icon_path_png):
                self.root.iconphoto(True, tk.PhotoImage(file=icon_path_png))

    def on_close(self):
        logging.shutdown()        
        if os.path.exists(self.log_file_path):
            os.remove(self.log_file_path)
        self.root.destroy()

if __name__ == "__main__":
    root = tk.Tk()
    gui = P2PGUI(root)
    root.mainloop()