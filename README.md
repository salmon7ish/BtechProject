# Menu Visuals
This is the demonstration video of the BTech project (June 2017), in which we created an Android app that can scan the options in the Restaurant menu card and give a visual description of it. This is meant to facilitate the users (especially the non-English users) that go to foreign places and don't understand the native language but still want to enjoy the authentic dishes. Briefly, we extract the menu item (food item) which is sent to a database as a query that maps a food item to its description. 
Here in the video, we show the translation of a Mexican dish to its Visual description.

We use Tesseract-OCR (https://github.com/tesseract-ocr/) for characted-by-character extraction of words, after the word is extracted, a table (MYSQL) is maintained which contains the list of menu items of a specific region(in this case mexican), this word is sent as a query to the table and matched using LCM algorithm. After mapping the visual results along with description are sent back. We maintain a small database in the application itself. The code can be changed for making use of remote database. 
 
# Requirements
 - AndroidStudio2.2
 - Tesseract-OCR
 - java

Note: This application was ran on Android 7 and 8 and might not be compatible with newer versions of Android.

https://user-images.githubusercontent.com/68821141/132671073-40b149c6-1d61-40aa-8e04-826bbb151689.mp4

# Buidling 

Make sure that Git path is specified on your Android studio.
On Android Studio welcome screen select "Get from Version Control", then clone this directory by clicking the clone button.

# Note 
This has deprecated and no longer maintained.
