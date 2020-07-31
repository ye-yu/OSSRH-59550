
/**
 * <h1>JAMCGUI - Just Another MC Gui</h1>
 *
 * Main module for creating GUIs using
 * widgets.
 * <hr>
 * Build your GUI with three components:
 * <ul>
 *     <li><b>Screen Renderer</b> - extended from <tt>HandledScreen</tt>,
 *     a client-side instance for storing widgets to be drawn
 *
 *     <li><b>Widget</b> - a listening object to handle user-to-handler
 *     interactions
 *
 *     <li><b>Screen Renderer Handler</b> - extended from <tt>ScreenHandler</tt>,
 *     a context handler between the client and the server
 * </ul>
 *
 * <p>
 * Supports networking between the client and
 * the server screen handler instances.
 * <p>
 * Related: {@linkplain io.github.yeyu.packet.ScreenPacket}
 * <p>
 * Contains entry-point class for Fabric to
 * register screen packets.
 * */
package io.github.yeyu;