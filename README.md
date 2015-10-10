# pongGame
This was my first java project.

Pong Game developed by Allan Alcorn, Atari in 1972.<br />
It is a computer tennis sports game.

The left side paddle is the user and the right side paddle is the computer(AI).<br />
The screen size, the ball size, the ball speed, each paddle size, the paddle speed, AI reaction time,<br />
and the scrore to win(or to finish the game) are all set as constants inside Pong.java<br />
<br />
There are Ball class and Paddle class to draw and control a ball and 2 paddle objects.<br />
It is Vector2 class initializing the position of each object and updating a movement of it.<br />
<br />
Pong.java has also inner class to implements keyListener and MounseMotionListener.<br />
The user can use both the keyboard and the mouse to control the paddle.<br />
<br />
JOGL(Java OpenGL) is used to render visualization.<br />
* OpenGL is an industry standard 2D/3D graphics APIs.<br />

Libraries: jogl-all-natives-windows-amd64.jar, gluegen-rt.jar, and jogl-all.jar 
<br />
Below is gif file of game playing demo. It does not look as much smooth as the actual game is.<br />
If you download the avi video file, you can see the better quality.

![gif file](https://raw.githubusercontent.com/alexpark90/pongGame/master/pongPlay.gif)

<a href="https://raw.githubusercontent.com/alexpark90/pongGame/master/pongPlay.avi">Click here to download</a>
