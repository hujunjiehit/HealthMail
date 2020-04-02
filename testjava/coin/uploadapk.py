# -*- coding: utf-8 -*-
import sys
from urllib3 import *
from bs4 import BeautifulSoup
import json
from dingtalkchatbot.chatbot import *
import dingtalkchatbot.chatbot as cb

def upload():
	syslen = len(sys.argv)
	if (syslen < 2):
		print("参数有误")
		return

	apkPath = sys.argv[1]

	print("apk path:" + apkPath)
	
	strArray = apkPath.split('-')
	version = strArray[2]
	print("version ========>" + version)

	uploadApkUrl = "http://admin.test.atomchain.vip/admin/content/oss/upload?fileType=app"

	http = PoolManager()
	with open(apkPath, 'rb') as fp:
		fileData = fp.read()
		print("apk size:",len(fileData))

	response = http.request('POST', uploadApkUrl, fields={'file':(apkPath,fileData,'application/vnd.android.package-archive')},headers={'site':'MAIN'})

	responseJson = json.loads(response.data.decode('utf-8'))

	if responseJson['code'] == 200:
		print("===>文件上传成功")
	else:
		print("===>文件上传失败", response.data.decode('utf-8'))
		return

	apkUrl = responseJson['data']

	generateQRcodeUrl = "https://cli.im/api/qrcode/code?text=" + apkUrl;
	response = http.request('GET', generateQRcodeUrl)

	soup = BeautifulSoup(response.data.decode('utf-8'), 'html.parser', from_encoding='utf-8')


	imgs = soup.find_all("img")


	print("\napk下载地址:" + apkUrl)
	print("\n二维码地址：http:" + imgs[0]['src'])

	imageUrl = 'http:' + imgs[0]['src']


	#下一步，发送消息到钉钉群 或者 发送邮件给测试

	#正式群机器人
	post_url = "https://oapi.dingtalk.com/robot/send?access_token=1f10227f5046896840172d9d6834f21bbf3d0823b24bd9d05287b5e49c2e2488"

	#测试群机器人
	post_url_test = "https://oapi.dingtalk.com/robot/send?access_token=d4b2905fd080c692aae40d82da295e79397eec61ea0622b41d0e0e7bb7e7cd4d"

	robot = cb.DingtalkChatbot(post_url)

	robot.send_markdown(title='android版本打包成功', text='## android {}版本打包成功\n'
							'> ![下载二维码]({})\n> ###### 扫码安装 或者 [点击下载apk]({}) \n'.format(version,imageUrl,apkUrl),
                           	is_at_all=False)

if __name__ == '__main__':
	upload()
