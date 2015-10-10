# pongGame
This was my first java project.

Pong Game developed by Allan Alcorn, Atari in 1972.
It is a computer tennis sports game.

The left side paddle is the user and the right side paddle is the computer(AI).
The screen size, the ball size, the ball speed, each paddle size, the paddle speed, AI reaction time,
and the scrore to win(or to finish the game) are all set as constants inside Pong.java

There are Ball class and Paddle class to draw and control a ball and 2 paddle objects.
It is Vector2 class initializing the position of each object and updating a movement of it.

Pong.java has also inner class to implements keyListener and MounseMotionListener.
The user can use both the keyboard and the mouse to control the paddle.

JOGL(Java OpenGL) is used to render visualization.
* OpenGL is an industry standard 2D/3D graphics APIs.

Libraries: jogl-all-natives-windows-amd64.jar, gluegen-rt.jar, and jogl-all.jar 

![gif file](https://raw.githubusercontent.com/alexpark90/pongGame/master/pongPlay.gif)
