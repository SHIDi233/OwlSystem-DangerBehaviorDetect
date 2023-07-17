import os
import oss2

access_key_id = 'LTAI5tMTJVU9jQjwDauxDzpn'
access_key_secret = 'epL3T48yhSkVxj70B548ZoYchf2QzW'

# 填写自己的 Bucket 名称和上传地址
bucket_name = 'dbd-video-0'
upload_path = 'uploads/'

# 创建 OSS 链接
auth = oss2.Auth(access_key_id, access_key_secret)
bucket = oss2.Bucket(auth, 'http://oss-cn-beijing.aliyuncs.com', bucket_name)


# 上传文件到 OSS
def oss_upload_file(file_path):
    # 构造上传路径
    file_name = os.path.basename(file_path)
    oss_path = upload_path + file_name
    # 上传文件
    with open(file_path, 'rb') as file_obj:
        result = bucket.put_object(oss_path, file_obj)
    # 返回上传地址
    return 'http://' + bucket_name + '.oss-cn-beijing.aliyuncs.com/' + oss_path

