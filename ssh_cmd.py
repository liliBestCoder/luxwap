import paramiko
import sys

import os

def run_remote_command(cmd):
    hostname = os.environ.get("SERVER_HOST", "101.201.215.20")
    username = os.environ.get("SERVER_USER", "root")
    password = os.environ.get("SERVER_PASSWORD")
    
    if not password:
        print("Error: SERVER_PASSWORD environment variable is not set!", file=sys.stderr)
        return
    
    ssh = paramiko.SSHClient()
    ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
    
    try:
        ssh.connect(hostname, port=22, username=username, password=password, timeout=10)
        print(f"--- Executing: {cmd} ---")
        stdin, stdout, stderr = ssh.exec_command(cmd)
        
        # Read output
        out = stdout.read().decode('utf-8', errors='replace')
        err = stderr.read().decode('utf-8', errors='replace')
        
        if out:
            print("STDOUT:")
            print(out)
        if err:
            print("STDERR:")
            print(err)
            
    except Exception as e:
        print(f"Error connecting or executing: {e}", file=sys.stderr)
    finally:
        ssh.close()

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Usage: python ssh_cmd.py <command>")
        sys.exit(1)
    
    cmd = " ".join(sys.argv[1:])
    run_remote_command(cmd)
