# ThinkingU-Doma

An application that provides the minimal user interface and steps to connect with your loved ones. The applications shows the contacts from the user's device that have this
application installed in their phone. The users then can select those contact's profile and can send them the notification. Users can either send the default message as a 
notification, or they can edit those default messages and can send their own message as a notification. There are three default messages to choose from.
Once a user sends a notificatoion to a contact, the send button gets disabled for an hour. A count down timer will be shown on the SEND button instead of SEND text message. \

# Working of application

1- Clone the repository and run the application on android studio. It should work by default. All of the necessary files and dependencies needed to run the application are added
   in the repository.
   
 2- In any case the application does not work then follow the following steps.
    - Open the application in android studio.
    - Open firebase.google.com in your browser and creat a new project in firebase console.
    - Go to android studio and select tools > Firebase > (Firebase assistant will open) Auhtentication > Authenticate using email and password > connect app to firebase and add       the firebase
    - Firebase > Realtime database > Get started with realtime database > Connect your app to firebase and add realtime database to your app
      authentication to your app. Refer to the screenshots below. 
      ![Screenshot 2022-01-11 012440](https://user-images.githubusercontent.com/97124152/148854482-de14f754-d232-4ba6-b4df-5dbf97df08bc.jpg)
      This way, firebase will automatically add google-services.json file to the application. 
      The google-services.json file should be in the following directrory as the screenshot below.
      ![google-services](https://user-images.githubusercontent.com/97124152/148854764-b05ea657-7928-41ef-bbd5-da0ed4c5d55a.jpg)
      If that does not show up, manually add google-services.json file to the project by referring the screenshots below.
     ![Screenshot 2022-01-11 010937](https://user-images.githubusercontent.com/97124152/148853412-ac258dbf-0c9f-44de-a329-88117d2e89f2.jpg)
     ![Screenshot 2022-01-11 0113582323242](https://user-images.githubusercontent.com/97124152/148853561-4c9be41b-4a3a-41ac-9b8e-ff6a4593e5dd.jpg)
     Then, go to the created project in firebase console and go to that project's settings. 
     ![Screenshot 2022-01-11 011530](https://user-images.githubusercontent.com/97124152/148853881-14b07b6f-3512-46c8-ab32-376cb9f5ee7a.jpg)
     ![Screenshot 2022-01-11 011914](https://user-images.githubusercontent.com/97124152/148853979-b4f27e64-4422-44be-b101-7988406f3715.jpg)
     ![Screenshot 2022-01-11 012940](https://user-images.githubusercontent.com/97124152/148854866-5eacc8b0-627a-4a01-bbe3-0d775d7ca4c8.jpg)
     Download this file and add to project. Now the app is connected to database. Try to run the app.
     - Once the app runs, run it in two phones in order to properly test it. register from both phones and make sure that the phone numbers of each phone is saved in the other
       phone. This is because the app only shows the people that are in your contact list and have the application installed. For example, if your phone number is 12345678 and
       you register with this phone number and your friends have this number installed in their phone and ur friend's phone number is 87654321 and they register with this phone
       number and you have this phone number saved in your phone, then both of you will be able to see each other's profile. Tap on the profile and make sure the other phone
       who you are sending notification to has the app in foreground.
       # Registration information.
       For registering, the password must be at least 6 characters long and all fields are required to register. If there is an email already registered, registering with the          same email again will give an error.
       The videos are provided below of working of application

https://user-images.githubusercontent.com/97124152/148857438-c23497bd-0565-49c4-867d-3f078a3c2334.mp4



https://user-images.githubusercontent.com/97124152/148857453-cd512300-eda3-4fa8-96a6-3b7330bf5a2d.mp4


      




      
