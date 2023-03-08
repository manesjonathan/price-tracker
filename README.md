# Whatsapp price tracker

This is a Java Spring boot application that tracks the price of a product on Amazon and sends a Whatsapp message when
the price drops below a certain threshold.

## Getting started

To get a local copy up and running follow these simple steps.

### Prerequisites

* Java 17
* Gradle
* A Twilio account
* A Whatsapp account
* A MongoDB account

### Installation

1. Clone the repo
   ```sh
   git clone https://github.com/manesjonathan/price-tracker.git
   ```
2. Create a Twilio account and get your account SID, auth token and Whatsapp sandbox number
3. Create a Whatsapp account and get your phone number
4. Create a MongoDB account and get your connection string
5. Create the application.properties files in the src/main/resources folder
   ```sh
   touch src/main/resources/application.properties
   ```
6. Add the following properties to the application.properties file
   ```sh
    spring.data.mongodb.uri=[YOUR_MONGODB_CONNECTION_STRING]
    spring.data.mongodb.database=[YOUR_MONGODB_DATABASE_NAME]
    twilio.account.sid=[YOUR_TWILIO_ACCOUNT_SID]
    twilio.auth.token=[YOUR_TWILIO_AUTH_TOKEN]
    custom.twilio.phone.number=[YOUR_TWILIO_WHATSAPP_SANDBOX_NUMBER]
   ```

## Usage

1. Run the application
   ```sh
   gradle bootRun
   ```
2. Send a Whatsapp message to your Twilio Whatsapp sandbox number with the following format
   ```txt
    https://www.amazon.com/Apple-MacBook-13-inch-256GB-Storage/dp/B08N5V3Q4S/ref=sr_1_1?dchild=1&keywords=macbook+pro&qid=1631620003&sr=8-1
    ```
3. The application will send you a Whatsapp message when the price drops below a certain threshold

## Author

* Jonathan Manes - [GitHub](https://github.com/manesjonathan)