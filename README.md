# Pdf_Office
Pdf Office android application

Prior knowledge of following are needed to run the application .

1 . kubernetes 

2 . GCP or aws 

3 . continous integration 

This repository contains the pdf office android application . 
The application uses firebase for authentication the authentication is currently disabled .

To run the app as reader simply download the apk and install.
To use the conversion feature configure the backend in kubernetes .
The backend repositories are :-
https://github.com/Shubhamr837/Pdf-to-Word-Server .
https://github.com/Shubhamr837/Pdf-Office-Eureka-Server .
https://github.com/Shubhamr837/Pdf-Office-Zuul-Proxy .

Clone the repo and build the application after going through following steps .

The installation can be done going through the readme of each repository .

Set the service ip address obtained after the installation of zuul proxy in com.shubhamr837.pdfoffice.utils.CommonConstants 
as Server_url variable .

For pdf view the application uses the following open source library
https://github.com/barteksc/AndroidPdfViewer
