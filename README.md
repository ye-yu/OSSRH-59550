# Just Another MC Gui (jamcgui)

A Fabric module for widget-based GUI for Minecraft

![Require Fabric Kotlin](https://i.imgur.com/c1DH9VL.png)

[![Maven Central](https://img.shields.io/maven-central/v/io.github.ye-yu/jamcgui.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.ye-yu%22%20AND%20a:%22jamcgui%22)

[LICENSE](LICENSE)

jamcgui presents a clearer control of GUI rendering and handling
using three main components:

- Screen Renderer - extended from `HandledScreen`, a client-side 
instance for storing widgets to be drawn
- Widget - a listening object to handle user-to-handler 
interactions
- Screen Renderer Handler - extended from `ScreenHandler`, a 
context handler between the client and the server

As a result, the vanilla `HandledScreen` is no longer in-charge
of event listening. If you want to build a GUI that interacts
with the gameplay, this module can be beneficial for you!

jamcgui also offers a utility file to perform networking
between the client and the server. 

# Installation guide

Add this path to your mod dependency
```groovy
dependencies { 
    modImplementation "io.github.ye-yu:jamcgui:0.0.1-alpha.4"

//  uncomment below to bundle jar
//    include "io.github.ye-yu:jamcgui:0.0.1-alpha.4"
}
```

```kts
dependencies {
    modImplementation("io.github.ye-yu:jamcgui:0.0.1-alpha.4")

//  uncomment below to bundle jar
//    include("io.github.ye-yu:jamcgui:0.0.1-alpha.4")
}
```

This mod is written in Kotlin (because kotlin is life uwu).
Read about Fabric Kotlin [here](https://www.curseforge.com/minecraft/mc-mods/fabric-language-kotlin).
If you don't bundle this mod, you can obtain jamcgui latest
release at the [release](release) page.
# Simplified Steps to Build Gui

You must extend the `ScreenRenderer` and 
`ScreenRendererHandler` class based on your requirement, and then
you have to register `ScreenRendererHandler` on the server-side
and `ScreenRenderer` on the client-side. Next, you add the
relevant widgets in the screen renderer. Finally, you
spawn the GUI on the relevant player action.

1. Extending & Registering `ScreenRendererHandler`

    You need to understand that a screen handler instance exists on 
    both client and server side. You can create two screen-handler
    classes each for the server and the client, extending a common 
    screen handler class. This can be useful for screen networking.
    The server screen handler must implements `ServerScreenHandlerPacketListener`
    while the client implements `ClientScreenHandlerPacketListener`.
    Otherwise, if you want to make both sides to instantiate the screen
    handler of the same class, you implement both interfaces on the
    same handler class (vanilla does this). More networking information is in the 
    [networking](#networking) section.

    Registering your new screen handler occurs in the fabric
    'main' mod initializer class. The registered reference
    must be accessible on the 'client' mod initializer too
    as a reference to register the screen renderer. In the factory parameter, the
    passed lambda expression must return the client screen handler
    given that you create separate screen handlers for the client
    and the server (otherwise, just return the same handler instance
    for server screen handler). The returned value is an instance of
    `ScreenHandlerType<your common super handler for client & server>`
    
    ```java
    public static ScreenHandlerType<ScreenRendererHandler> MY_CUSTOM_SCREEN_HANDLER;
    ...
    // in ModInitializer
    MY_CUSTOM_SCREEN_HANDLER = ScreenHandlerRegistry
            .registerSimple(<Identifier>,
                    (int syncId, PlayerInventory inventory) -> {
                        return new ClientScreenHandler(MY_CUSTOM_SCREEN_HANDLER, ...)
                        /* or below if don't create separate handler
                           client & server
                         */
                        return new ClientAndServerScreenHandler(MY_CUSTOM_SCREEN_HANDLER, ...)
                }
            );
    ```
   
   Related reference:
     - [(Kotlin) Extending client screen handler](guitest/src/main/kotlin/handler/ClientInventoryHandlerImpl.kt)
     - [(Kotlin) Extending server screen handler](guitest/src/main/kotlin/handler/ServerInventoryHandlerImpl.kt)
     - [(Kotlin) Registering `blockScreen`](guitest/src/main/kotlin/Screens.kt)

2. Extending & Registering `ScreenRenderer`

    Extend `ScreenRenderer` and override `init()` method. In
    this method, you put in the relevant parent widgets and
    listeners into the renderer instance. Parent widgets are
    panels, etc, and they take in child widgets. 
    For this reason, screen renderer is not responsible
    for drawing child widgets, and we delegate this take to
    parent widgets instead. So, screen renderer must take in parent
    widget instance through `addParent` method.

    Widgets that listen to client event must be added 
    separately using `addListener` regardless of widget type
    (parent or child). For example, text field widget listens
    to key input, so remember to add this to the screen
    renderer listeners. Label widgets are not a kind of
    listener, so they should not be added to the listeners.

    When you are finally done with the widgets, you register
    the screen renderer into the client mod initializer.
    Pass in the screen handler type registered in the
    'main' mod initializer in the previous step.
    
    ```java
    ScreenRegistry.<ScreenRendererHandler, BlockScreenRenderer>register(
            MY_CUSTOM_SCREEN_HANDLER,
            (screenRendererHandler, playerInventory, text) -> new BlockScreenRenderer(screenRendererHandler, playerInventory, text)
    );
    ```

   Related reference:
     - [(Kotlin) Extending client screen renderer](guitest/src/main/kotlin/BlockScreenRenderer.kt)
     - [(Kotlin) Registering client screen renderer](guitest/src/main/kotlin/Screens.kt)

3. Spawning GUI

    You can spawn the GUI through various actions such as on
    block use, on entity interactions, etc. For convenience,
    the entity must contain a singleton of 
    `NamedScreenHandlerFactory`, which can be implemented in
    any class. In the class implementation, the 
    implemented method `createMenu` must return the
    corresponding server screen handler instance.
    
    ```java
   	@Override
   	public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
   		return new ServerScreenHandler(MY_CUSTOM_SCREEN_HANDLER, syncId, inv);
        /* or below if don't create separate handler
           client & server
         */
        return new ClientAndServerScreenHandler(MY_CUSTOM_SCREEN_HANDLER, ...)
   	}
    ```
    
    To spawn the GUI, you pass the singleton into 
    `ServerPlayerEntity#openHandledScreen` method.
    
    ```java
    private final NamedScreenHandlerFactory customGuiFactory = <some implemented class>
    ...
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hitResult) {
        player.openHandledScreen(customGuiFactory);
        return ActionResult.SUCCESS;
    }
    ```
   
   Related reference:
     - [(Kotlin) Create block entity ](guitest/src/main/kotlin/GuiBlockEntity.kt)
     - [(Kotlin) Spawn gui on block use ](guitest/src/main/kotlin/GuiBlock.kt)
     

## <a name="networking"></a> Networking

Networking can be useful for GUI that manipulates
gameplay. The server relies on the client packets
to drive the gameplay, and the client also relies
on packets to render the gameplay on the screen.

The first thing to remember in networking is
handshakes. You can't send packets to client
if client has not initialised a GUI. The package
provided a utility file [ScreenPacket](src/main/kotlin/io/github/yeyu/packet/ScreenPacket.kt)
for a manageable packet communication. In this
example, we are going to send an 'init' packet 
to the server to tell that the client can now
receive screen-related packets.

To send an 'init' packet to the server, the client
can use the method `ScreenPacket#sendPacket`. The
buffer wrapper parameter is a consumer lambda 
to append any extra information to the server
screen handler. Since this packet is for the server, 
the player parameter can be left null.

```java
ScreenPacket.INSTANCE.sendPacket(syncId, "init", true, null, it -> {
    it.writeString("I have inited");
    return null;
});
```

On the server screen handler, the packet
must be handled in `onClient2Server` method

```java
public void onClient2Server(String action, PacketContext context, PacketByteBuf buf) {
    if (action.equalsIgnoreCases("init")) {
        PlayerEntity player = context.getPlayer();
        String msg = buf.readString();
        System.out.println(player.getEntityName() + " has inited their GUI. They says: " + msg);
    }
}
```

'init' packet can be useful to start sending properties
that is not easily accessible on the client-side
such as entity inventories. The client does not
have access to the server entity instance, so it
cannot access the entity inventories as soon as
the client open an entity GUI. Since the entity
instance resides on the server-side, the server
can read the entity inventories information and 
broadcast this information to the client. 
However, if the server sends the packets before
the client gets to open the entity GUI, the packet
will be lost. That is why 'init' packet is crucial.

There are several other ways packets can be used
to manipulate gameplay. Take a look at the example
from the appended related reference.

Related reference:
  - [(Kotlin) Extending client screen handler](guitest/src/main/kotlin/handler/ClientInventoryHandlerImpl.kt)
  - [(Kotlin) Extending server screen handler](guitest/src/main/kotlin/handler/ServerInventoryHandlerImpl.kt)


# Footnote

There are more pre-implemented widgets and screen handlers
that could be useful for most cases. If you are creating
widgets, `ChildWidget` and `ParentWidget` are the available
options. Widget listens to client events but can query
handler data too. Implement [provider interfaces](src/main/kotlin/io/github/yeyu/gui/handler/provider)
on the client(!) handler, and cast handler type to the implemented
provider interface to run the interface method. Handler
but return the appropriate value based on the passed "name"
value.

While listening to events, widget can broadcast update to
the client handler too. Implement [handler listener interfaces](src/main/kotlin/io/github/yeyu/gui/handler/listener)
on the client(!) handler. The client handler usually have to
send the received value to the server handler so that the
server can compute the correct action.

As a rule of thumb, implement provider if you are only conveying
and drawing information from the server to the client.
Implement listener if you want to send client events to
the server.


<small>!: Must be a client handler</small>

A wiki will be added for future public reference.