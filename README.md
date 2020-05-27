# Enlaces de interés sobre LibGDX

## Documentación y tutoriales escritos
* [Wiki de LibGDX](https://github.com/libgdx/libgdx/wiki) 
en algunos casos carece de ejemplos prácticos, más allá de snippets de códigos
* [Tutoriales de GameFromScratch.com](https://www.gamefromscratch.com/page/LibGDX-Tutorial-series.aspx)
buenos ejemplos, aunque a veces está algo desfasado
* [Full Libgdx Game Tutorial](https://www.gamedevelopment.blog/full-libgdx-game-tutorial-flgt-home/)
Tutorial extenso sobre la realización de un juego desde cero, con scene2dUI, Box2D, partículas, ECS Ashley, IA …
* [Documentación oficial de Box2D](https://box2d.org/documentation/)
Más extenso que el capítulo en la wiki de LibGDX. Los ejemplos están en C++ pero la API es la misma

## Tutoriales en vídeo
* [Flappy Bird](https://www.youtube.com/watch?v=rzBVTPaUUDg&list=PLZm85UZQLd2TPXpUJfDEdWTSgszionbJy)
Un buen punto de partida. Es el tutorial más básico que he encontrado sobre un juego completo.
* [Super Mario Bros](https://www.youtube.com/watch?v=a8MPxzkwBwo&list=PLZm85UZQLd2SXQzsF-a0-pPF6IWDDdrXt)
Incluye creación de mapas con Tiled y uso de Box2d
* [Slide15](https://www.youtube.com/watch?v=S-wCEUO_dY8&list=PLD_bW3UTVsEkPsT2JfVcZmAjmWByIpRvT&index=1)
Juego de puzzle deslizante con scene2d y scene2d.ui. Se aplican buenas prácticas en el desarrollo con libgdx (ahorro de recursos, organización del código en los sitios adecuados). La pega es que pone toda la lógica del juego en las pantallas y tiene que llevar la cuenta, por separado de la posición de las fichas tanto en el modelo como en la vista (actualizando el array bidimensional y realizando el movimiento en pantalla)

## Herramientas
* [LibGDX tools](https://libgdx.badlogicgames.com/tools.html)
Aquí podéis encontrar aplicaciones como Texture Packer y 2D Particle Editor
* [Skin Composer](https://ray3k.wordpress.com/software/skin-composer-for-libgdx/)
Un editor de skins para Scene2d.ui
* [gdx-liftoff](https://ray3k.wordpress.com/software/skin-composer-for-libgdx/)
Herramienta de setup de proyectos Libgdx más moderna y completa que la oficial
* [Tiled](https://www.mapeditor.org/) 
Editor de mapas 2D
* [gdx-dbgagent](https://github.com/PokeMMO/gdx-dbgagent)
Agente java para depurar problemas de LibGDX relacionados con la gestión de recursos nativos

## Recursos gratuitos
* [opengameart.org](https://opengameart.org/) Multitud de recursos, sobre todo "pixelart"
* [Kenney assets](https://kenney.nl/) Más calidad, aunque menos cantidad
* [Character Generator](http://gaurav.munjal.us/Universal-LPC-Spritesheet-Character-Generator/)

## Juegos open source
* [Super Mario](https://github.com/arjanfrans/mario-game) 
ejemplo de super mario usando scene2d y actores
* [Otro Super Mario](https://github.com/maheshkurmi/libgdx-mario) 
otro super mario, más "artesanal" (está portado de un proyecto anterior sin motor de juego, por lo que hay mucha “rueda reinventada” sobre características que ya te da libgdx)
* [Uniciv](https://github.com/yairm210/Unciv) Civilization para android y escritorio hecho en kotlin con scene2d

## Entity Component Systems
* [Understanding ECS](https://www.gamedev.net/tutorials/_/technical/game-programming/understanding-component-entity-systems-r3013/) 
* [Qué es un ECS](https://www.richardlord.net/blog/ecs/what-is-an-entity-framework.html) Explica qué motivó su aparición. Aunque a lo largo del artículo tiende a confundirse OOP con un sistema basado exclusivamente en herencia. En mi opinión, la potencia de los ECS radica en el rendimiento, más que en el diseño.
* [Por qué usar un ECS](https://www.richardlord.net/blog/ecs/why-use-an-entity-framework.html)
* [Documentación de Ashley](https://github.com/libgdx/ashley/wiki) ECS “oficial” de libdgx
* [Ashley y Scene2d](https://javadocmd.com/blog/libgdx-ashley-on-the-stage/) Artículo que explica cómo pueden convivir Scene2D y Ashley y qué ventajas supone esto.
* [OOP ECS vs pure ECS](https://softwareengineering.stackexchange.com/questions/369066/oop-ecs-vs-pure-ecs)
Discusión entre dos enfoques de ECS (orientado a objetos vs un enfoque más puro)
* [Experiencia positiva con ECS en un juego MMORPG](https://www.reddit.com/r/libgdx/comments/618daq/anyone_made_a_game_using_ashley_ecs/)
* [Ashley y Box2D](https://www.gamedevelopment.blog/ashley-and-box2d-tutorial/)
* [Should I implement entity component system in all my projects?](https://gamedev.stackexchange.com/questions/114301/should-i-implement-entity-component-system-in-all-my-projects) 
Interesantísima respuesta sobre cuándo se debería diseñar un juego con ECS y cuándo no sería necesario. Incluye una discusión entre quien da la respuesta (pragmático) y otro usuario defensor del uso de ECS en cualquier juego, por mantenibilidad.
* [Benchmark de ECS’s en java](https://github.com/junkdog/entity-system-benchmarks#entity-system-benchmarks) 
ashley no es la mejor opción en cuanto a performance, aunque es la que más documentación tiene, al estar “adoptada” oficialmente por libgdx

## Material adicional
* [Cálculos básicos para la física de los saltos](https://medium.com/@brazmogu/physics-for-game-dev-a-platformer-physics-cheatsheet-f34b09064558)
Para poder implementar saltos sin usar Box2D
* [Fix your Timestep](https://gafferongames.com/post/fix_your_timestep/)
Artículo sobre el uso de steps fijos en las simulaciones físicas de Box2D
* [Física de coches](https://asawicki.info/Mirror/Car%20Physics%20for%20Games/Car%20Physics%20for%20Games.html)
 (Hay mucho trabajo más allá de llamar a Box2D)
* [Awesome LibGDX](https://project-awesome.org/rafaskb/awesome-libgdx)
Colección de material relacionado con LibGDX
