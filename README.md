# 서울시립대학교 내장형시스템및실습(2021-1학기)
<div class = "shields" style = "display: flex; "> 
    <img src = "https://img.shields.io/github/issues/only-juun/6x3equals18">
    <img src = "https://img.shields.io/github/forks/only-juun/6x3equals18">
    <img src = "https://img.shields.io/github/stars/only-juun/6x3equals18">
    <img src="https://img.shields.io/static/v1?label=ESE&message=BIG-BOX" />
    <img src="https://img.shields.io/github/languages/top/only-juun/6x3equals18" />
    <img src="https://img.shields.io/github/languages/count/only-juun/6x3equals18" />
    <img src="https://img.shields.io/github/last-commit/only-juun/6x3equals18"/>
    <img src="https://img.shields.io/github/license/only-juun/6x3equals18" />
    <img src="https://hits.seeyoufarm.com/api/count/incr/badge.svg?url=https%3A%2F%2Fgithub.com%2Fonly-juun%2F6x3equals18&count_bg=%2379C83D&title_bg=%23555555&icon=&icon_color=%23E7E7E7&title=hits&edge_flat=false"/>
</div>

## 팀명
6x3equals18

## 개발 주제: BIG(Barcode-Identification-Guaranteeing) Private Storage Box
바코드 인증 방식의 개인용 무인 택배함 시스템 설계

## 개발 기간
2021.04.05. ~ 2021.06.21.

## 구성원
||학번 |이름|github username|
|--|--|--|--|
|팀장|20164300**|이*하|<a href = "https://github.com/lmh970329">lmh970329</a>|
|팀원|20164300**|남*준|<a href = "https://github.com/only-juun">only-juun</a>|
|팀원|20164300**|박*호|<a href = "https://github.com/Parkjhjh">Parkjhjh</a>|
|팀원|20184300**|최*리|<a href = "https://github.com/Deb0r4h">Deb0r4h</a>|

## 최종보고서
https://capstone.uos.ac.kr/mie/index.php/6x3%3D18_-_BIG_Private_Box

## 어플리케이션(android_application)
### 어플리케이션 화면구성
<img src = "https://user-images.githubusercontent.com/79013722/122664590-550fab80-d1dd-11eb-93b9-8caeae00fe37.jpg" width="20%" height="20%"> <img src = "https://user-images.githubusercontent.com/79013722/122664603-66f14e80-d1dd-11eb-8033-7aa04f70d1bb.jpg" width="20%" height="20%"> <img src = "https://user-images.githubusercontent.com/79013722/122664609-6ce72f80-d1dd-11eb-8d6b-eeeb8996642f.jpg" width="20%" height="20%"> <img src = "https://user-images.githubusercontent.com/79013722/122664640-8daf8500-d1dd-11eb-9c50-a7f934ef294d.jpg" width="20%" height="20%"> <img src = "https://user-images.githubusercontent.com/79013722/122664612-72447a00-d1dd-11eb-9c06-59a966913c58.jpg" width="20%" height="20%"> <img src = "https://user-images.githubusercontent.com/79013722/122664616-74a6d400-d1dd-11eb-8819-200238fb7864.jpg" width="20%" height="20%"> <img src = "https://user-images.githubusercontent.com/79013722/122664630-82f4f000-d1dd-11eb-8477-f068a39748de.jpg" width="20%" height="20%"> <img src = "https://user-images.githubusercontent.com/79013722/122664618-783a5b00-d1dd-11eb-84fc-348474479a3e.jpg" width="20%" height="20%"> <img src = "https://user-images.githubusercontent.com/79013722/122664621-7a9cb500-d1dd-11eb-8dac-149f575f5089.jpg" width="20%" height="20%"> <img src = "https://user-images.githubusercontent.com/79013722/122664627-7e303c00-d1dd-11eb-992f-b4f0b7df4bbd.jpg" width="20%" height="20%"> <img src = "https://user-images.githubusercontent.com/79013722/122664629-7f616900-d1dd-11eb-83b9-bdc9192851fa.jpg" width="20%" height="20%">

*QR코드 생성* - Zebra Crossing(Zxing)라이브러리 활용 [Zxing]<https://github.com/zxing/zxing>

## 도난(진동)감지 모듈(hardware/sensor_module)
### 도난 감지 모듈 구성
<img src = "https://user-images.githubusercontent.com/79013722/122668328-6fa04f80-d1f2-11eb-8854-0a1cba691f62.png" width="50%" height="50%">

### 도난 감지 센서값 설정
<img src = "https://user-images.githubusercontent.com/79013722/122668339-762ec700-d1f2-11eb-8a32-56d3896ba417.png" width="50%" height="50%">

### 도난 감지 알고리즘 플로우차트
<img src = "https://user-images.githubusercontent.com/79013722/122673519-c7978000-d20b-11eb-8b0b-7ff584b930b3.png">

## 바코드 스캔 모듈(db_pi) & 잠금장치 모듈(hardware/locking_module)
### 바코드 스캔 모듈 시나리오
<img src = "https://user-images.githubusercontent.com/79013722/122673504-b189bf80-d20b-11eb-83d1-d686e23ed6f4.png">

                                                                                                              
