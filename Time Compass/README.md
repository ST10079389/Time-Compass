# Time Compass Application 

## Team Members
- Avish Judnarain, ST10030291
- Kaushil Dajee, ST10079389
- Tivolan Velayadum, ST10183038
- Eben Nkulu Mwema, ST10091324

## Updates since Part 2
We have incorporated all feedback from part 2 such as moving features from the home page to keep it less congested and took all of sirs advice. We also included more detailed error handling.
The application now entirely uses Google firebase as the database to store data and lists have been removed. Data is securely saved such as passwords and no user can see another users data. We have added features like setting a profile picture for a user, a timer to keep track of your time and ability to view visual progress of daily goals. The Game page is now up and running which features a leaderboard ranking various users based on how much they have used the app, The stats page is also implemented wonderfully with not just one but two graphs being a bar and line graph. We have also massively updated the UI of the application as a big focus of ours to make the app more user friendly. We are using 3 firebase products, firebase authentication for the login and sign up for accounts, the real time database for categories, tasks, daily goal, points and profile, and we used firebase storage to save task images and the profile pictures image.

## Installation
Clone this repository to your local machine using git clone <repository_url>.
Open the project in Android Studio.
Build and run the app on your Android device or emulator.

## How to use our App
When you open the app you will be required to either Sign Up for a new account or Log In. 
These are details(Email, Password) of already registered users to login:
  (avish1@gmail.com, avish123),
  (kaushildajee@gmail.com, kaushil),
  (tivolan@gmail.com, tivolan),
  (eben@gmail.com, @H3ll0&By3)

On the Home page you will be greeted by a message with your desired username,
A tracker will start to monitor the time spent using the app on the profile page.
You are able to View All Tasks and Categories created on this page, as well as
set a daily goal for the amount of hours you wish to spend using the app which resets everyday.

If you want you can explore the app via the bottom navigation bar to the Home, Game, Stats and Profile Pages.
This is also where you can add a new Category or Task by clicking the plus icon and following the steps which will save these in Google firebase.
You will also be able to View these after creating them and also filter them after creation.
  
## Feature 1
Feature 1 is the game page, which works based on the number of tasks and categories you have, for every task you will get 100 points and for every category you will get 50 points. And it shows in descending order based on the user with the most points as it read our 
real time database and disaplys the correct data according to the number of points the users have.

## Feature 2
For Feature 2 we added a timer to help user track their time while completing tasks, we added a delete feature for tasks when our view tasks, click the task and it will take you to the current task and there will be a delete icon for you to delete a task if you want to and we added another feature which is the ability for users to have a profile picture so in total we added 3 features.

## Who implemented what ?
### Avish Judnarain, ST10030291
Worked on the Login/SignUp pages, Home page, Game page, Profile page as well as setting daily goals, animation when the app starts, viewing daily goal progress, timer feature, 
staying logged in to the app if already authenticated and ability to sign out, filtering tasks and categories and alot of focus on the UI. Planning and Design for theory in Part 1.
### Kaushil Dajee, ST10079389
Worked on the Add Category, View Category, Add Task, View Task, Filter Tasks, Game Page, adding a profile picture to the profile and helped with mostly back end features such as the confetti effect for the game page. Planning and Design for theory in Part 1. And added the delete task feature. 
### Eben Nkulu Mwema, ST10091324
Worked on everything involving the statistic's page, bug bounties and recorded the demostration videos. Research for theory in Part 1.
### Tivolan Velayadum, ST10183038
Research for theory in Part 1.

Thank you for using our application :) 
