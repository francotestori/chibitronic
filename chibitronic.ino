// -----------------------------------
// Controlling LEDs over the Internet
// -----------------------------------

// First, let's create our "shorthand" for the pins
// Same as in the Blink an LED example:
// led1 is D0, led2 is D7

int red = A5;
int blue = A4;
int green = D0;


// Last time, we only needed to declare pins in the setup function.
// This time, we are also going to register our Spark function

void setup()
{

   // Here's the pin configuration, same as last time
   pinMode(red, OUTPUT);
   pinMode(blue, OUTPUT);
   pinMode(green, OUTPUT);

   // We are also going to declare a Spark.function so that we can turn the LED on and off from the cloud.
   Spark.function("lightRed",lightRed);
   Spark.function("lightBlue",lightBlue);
   Spark.function("lightGreen",lightGreen);

   // This is saying that when we ask the cloud for the function "led", it will employ the function ledToggle() from this app.

   // For good measure, let's also make sure both LEDs are off when we start:
   analogWrite(red,0);
   analogWrite(blue,0);
   analogWrite(green,0);

}

// Last time, we wanted to continously blink the LED on and off
// Since we're waiting for input through the cloud this time,
// we don't actually need to put anything in the loop

void loop()
{
   // Nothing to do here
}

// We're going to have a super cool function now that gets called when a matching API request is sent
// This is the ledToggle function we registered to the "led" Spark.function earlier.

  int lightRed(String command){
      analogWrite(red,command.trim().toInt());
      return 1;
  }

  int lightBlue(String command){
    analogWrite(blue,command.trim().toInt());
    return 1;
  }

  int lightGreen(String command){
    analogWrite(green,command.trim().toInt());
    return 1;
  }