from watchdog.observers import Observer
from watchdog.events import FileSystemEventHandler
import time
from oss.oss import oss_upload_file


class CreatEventHandel(FileSystemEventHandler):
    def on_created(self, event):
        if event.is_directory:
            pass
        else:
            print(event.src_path)
            print(oss_upload_file(event.src_path))


def watching():
    dir_path = '.\\runs\\detect\\test'
    observer = Observer()
    event_handel = CreatEventHandel()
    observer.schedule(event_handel, dir_path, True)
    observer.start()
    # try:
    #     while True:
    #         time.sleep(1)
    # finally:
    #     observer.stop()
    #     observer.join()

# if __name__=='__main__':
#     dir_path='.\\runs\\detect\\test'
#     observer = Observer()
#     event_handel = CreatEventHandel()
#     observer.schedule(event_handel,dir_path,True)
#     observer.start()
#     try:
#         while True:
#             time.sleep(1)
#     finally:
#         observer.stop()
#         observer.join()
