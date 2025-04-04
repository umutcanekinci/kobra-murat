Patterns that I learned and used in that project:

Observer Pattern

Singleton Pattern
Board, Utils, Client and Server classes are used only once so it is good idea to make them static.


SERVER-SIDE PROGRAMMING
Collision detection.
Apple managment.
After game logic send the positions and directions.

CLIENT-SIDE PROGRAMMING (Never trust to a client)
Send input to the server. (Direction)
Handel UI events.
Simulate the game for delay in order to make game smoother. (Advanced)


METHOD ORDERING
Class (static) variables: First the public class variables, then the protected, and then the private.

Instance variables: First public, then protected, and then private.

Constructors

Methods: These methods should be grouped by functionality rather than by scope or accessibility. For example, a private class method can be in between two public instance methods. The goal is to make reading and understanding the code easier.


BUG Buttons are not scaling correctly. They work fine in just 1080p.
    There are mathmetical errors in layout. Need to use different layout or figure out the problem.
BUG FPS IS NOT STABLE.

     LOBBY
TODO Kick system in Lobby
TODO Configure system in lobby
TODO Player names, player skins


HELPS
https://www.reddit.com/r/gamedev/comments/8tdmaa/question_about_server_vs_client/
https://www.quora.com/Do-games-use-server-side-programming-or-client-side-programming

<h1>Tileset</h1>
https://opengameart.org/content/snake-sprites-2d