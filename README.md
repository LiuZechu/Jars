# Jars: Learn through memory
Learning content knowledge from reading text materials is difficult, but so much of learning starts from reading. There is a treasure trove of digital learning materials (e.g. textbooks, articles, research papers), and we want to make full use of this potential.

Jars is an Android mobile app that allows the user to highlight phrases or sentences while reading through any text material, from Google Documents to PDFs to random articles on the internet, which automatically form flashcards with a button drawn over the screen. The app prompts the user to review the flashcards at increasing intervals of time according to spaced repetition. They see the flashcards less as they get more familiar with their content.

Flashcards are represented as candies that must be trained and can graduate (after getting the candy right seven times). Users are encouraged to consistently make and review their candies through a gamified level/exp/sugar currency system.

## Authors
* [Ong Yan Chun](https://github.com/yaaanch)
* [Liu Zechu](https://github.com/LiuZechu)

## Goal
To make learning content knowledge from text materials convenient, efficient and effective.  
* Convenient = Accessible from your phone, portable, on-the-go  
* Efficient = Want to make cards quickly, review cards quickly  
* Effective = Consistent. Good for memory retention  

## Video demo
[click here for video demo](https://drive.google.com/open?id=1X7Ny4u_FHCcaZuly0BNPbAnLZH6SWwKj)

## What we have achieved
1. Back-end 
   1. New Addition: Users can open a floating window from the Main Tab of our app. The app will be sent to the background automatically, leaving only a floating icon on the foreground. Users can freely browse websites, PDFs, word documents etc. outside the app. When they wish to make a Candy (flash-card), they can select a portion of the text, click “copy”. Then they can access the “Make a Candy” button through the floating icon, which expands into three buttons upon tapping. The leftmost button minimises the floating window, the middle one brings users directly to the app, where they can fill in a prompt and choose a Jar (category) for their Candy. The answer portion is auto filled with text from the clipboard. Finally, users click the DONE button to save the Candy and automatically return to the previous app to continue reading. The third button on the floating window brings the user back to Jars Tab of the app, where they can view all their Jars and Candies made. We’ve decided to implement this feature instead of limiting the app to only PDF documents because through our user interviews, many prospective users have said that they don’t just use PDFs to learn. They use a combination of resources such as PDFs, web pages, word documents etc. Our final implementation allows for greater flexibility for learning resources.
   1. New Addition: the app enables users to attach a screenshot to a Candy. When they highlight the text, though not compulsory, they may choose to take a screenshot of the context of the highlighted phrases, and attach it to a Candy in the “Make a Candy” Page. They can do so through in-app Candy-making button as well. During training, they can view the screenshot by tapping the photo icon in the middle (as shown).
   1. New Addition: Candies can now be deleted from their respective Jars. An alert window will pop up to let users confirm before deletion.
   1. Jars and Candies: The Jar and Candy system is fully complete. Users can create candies, place them into jars, and train them as per spaced repetition. The candies can graduate and enter the archive.
   1. Profile and Settings: The user can view their statistics, view their achievements, and change their settings (e.g. their username, reminder time.
   1. Background Services: The countdown of the candies will automatically decrease as the days pass, handling the spaced repetition in the background. Users will be reminded to do their training at the alarm time that they have set.
   1. Gamified System: The exp, level, and sugar currency system has been set up. Users get appropriate exp and sugar as they create candies and complete their training.
   1. PDF: PDF files can be read.
1. Front-end
   1. New Addition: All assets from icons to expressions to images were all made by us! We have a total of >150 drawable files: >60 expressions, many many icons, makers, powerups, etc.
   1. New Addition: Floating service button that draws over other apps. Can minimize/expand.
   1. New Addition: Jars shelf with randomly selected colors and jar types for each jar on the shelf for customisation.
   1. New Addition: Each candy is generated with a random expression from >60 expressions. 
   1. New Addition: Color of the candy now represents its level!
   1. New Addition: Revamp to literally ALL of the UI of all pages. New look for everything! 
   1. New Addition: Inventory system allows you to look at your items. (Need to be added for colors and jars)
   1. Nav bars: Top bar and bottom bar
   1. Candy Makers: There are 3 types of candy makers that produce a random powerup/expression/color/jar.
   1. Achievements: Each achievement has progress bars, starred progress, etc.

## Acknowledgements 
This project was done under NUS Orbital 2019.

