#define rightMotorSpeed 3
#define leftMotorSpeed 5
#define rightMotor1 8
#define rightMotor2 7
#define leftMotor1 13
#define leftMotor2 12

int SPEED = 150;

char input;
int command = 0;

bool isMoving = false;
bool isTurningR = false;
bool isTurningL = false;

void setDirectionForward(int motorA, int motorB){
  digitalWrite(motorA, LOW);
  digitalWrite(motorB, HIGH);
}

void setDirectionBackward(int motorA, int motorB){
  digitalWrite(motorA, HIGH);
  digitalWrite(motorB, LOW);
}

void setup() {
  pinMode(rightMotor1, OUTPUT);
  pinMode(rightMotor2, OUTPUT);
  pinMode(rightMotorSpeed, OUTPUT);
  pinMode(leftMotor1, OUTPUT);
  pinMode(leftMotor2, OUTPUT);
  pinMode(leftMotorSpeed, OUTPUT);

  Serial.begin(9600);
}

void loop() {
  if(Serial.available() > 0){
    input = Serial.read();
    Serial.println(input);
    command = input - '0';
  }

  // forward
  if(command == 1){
    setDirectionForward(leftMotor1, leftMotor2);
    setDirectionForward(rightMotor1, rightMotor2); 
    analogWrite(rightMotorSpeed, SPEED);
    analogWrite(leftMotorSpeed, SPEED);
  }
  if(command == -1){
    analogWrite(rightMotorSpeed, 0);
    analogWrite(leftMotorSpeed, 0);
  }

  // backward
  if(command == 3){
    setDirectionBackward(rightMotor1, rightMotor2);
    setDirectionBackward(leftMotor1, leftMotor2);
    analogWrite(rightMotorSpeed, SPEED);
    analogWrite(leftMotorSpeed, SPEED);
  }
  if(command == -3){
    analogWrite(rightMotorSpeed, 0);
    analogWrite(leftMotorSpeed, 0);
  }

  // right
  if(command == 2){
    analogWrite(rightMotorSpeed, SPEED - 50);
    analogWrite(leftMotorSpeed, SPEED + 30);
  }
  if(command == -2){
    analogWrite(rightMotorSpeed, 0);
    analogWrite(leftMotorSpeed, 0);
  }

  // left
  if(command == 4){
    analogWrite(rightMotorSpeed, SPEED + 30);
    analogWrite(leftMotorSpeed, SPEED - 50);
  }
  if(command == -4){
    analogWrite(rightMotorSpeed, 0);
    analogWrite(leftMotorSpeed, 0);
  }
}