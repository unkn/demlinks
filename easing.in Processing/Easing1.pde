/**
 * Easing. 
 * 
 * Move the mouse across the screen and the symbol will follow.  
 * Between drawing each frame of the animation, the program
 * calculates the difference between the position of the 
 * symbol and the cursor. If the distance is larger than
 * 1 pixel, the symbol moves part of the distance (0.05) from its
 * current position toward the cursor. 
 */
 
float x;
float y;
float easing = 0.05;
float m=4;

void setup() {
  size(640, 360); 
  noStroke();  
  //strokeWeight(10);
  //color();
  stroke(128);
}

void draw() { 
  background(51);
  
  float targetX = mouseX;
  float dx = targetX - x;
  if(abs(dx) > 1) {
    x += dx * easing;
  }
  
  float targetY = mouseY;
  float dy = targetY - y;
  if(abs(dy) > 1) {
    y += dy * easing;
  }
  
  
  
  stroke(255);
  ellipse(x, y, 66, 66);
  stroke(204, 102, 0);
  strokeWeight(10);
  line(x,y,targetX,targetY);
  noStroke();
  strokeWeight(5);
  stroke(#CCFFAA);
  line(x,y, x+(m*dx* easing), y+(m*dy* easing) );
  
 /*float _x=width/m;
  float _y=height/m;
  line(_x,_y,_x+m* (dx* easing),_y+ m* (dy* easing) );
  */
}
