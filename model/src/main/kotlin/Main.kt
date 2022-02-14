//import com.google.protobuf.Descriptors
//import com.google.protobuf.JavaType
//import java.nio.file.Files
//import java.nio.file.Paths
//
//fun main(args: Array<String>) {
//    for (messageType in Model.getDescriptor().messageTypes) {
//        processAgentMessageDescriptor(messageType)
//    }
//    println("dff")
//}
//
//fun processAgentMessageDescriptor(descriptor: Descriptors.Descriptor) {
//    println(descriptor.name)
//    val snapshotDescriptor = descriptor.nestedTypes.find { it.name == "Snapshot" }!!
//    println(snapshotDescriptor.name)
//    for (field in snapshotDescriptor.fields) {
//        println(field.name + " " + JavaType.valueOf(field.javaType.name).type.kotlin)
//    }
//    println()
//    val behaviourDescriptor = descriptor.nestedTypes.find { it.name == "Behaviour" }!!
//    println(behaviourDescriptor.name)
//    for (field in behaviourDescriptor.fields) {
//        println(field.name + " " + JavaType.valueOf(field.javaType.name).type.kotlin)
//    }
//    println()
//
//    val setters = mutableListOf<Descriptors.Descriptor>()
//    val actions = mutableListOf<Descriptors.Descriptor>()
//    behaviourDescriptor.nestedTypes.forEach {
//        if (it.name.startsWith("Set")) {
//            setters += it
//        } else {
//            actions += it
//        }
//    }
//    println("Setter messages:")
//    setters.forEach {
//        println(it.name)
//    }
//    println()
//    println("Action messages:")
//    actions.forEach { println(it.name) }
//
//}
//
//

//fun main() {
//// Create a window called "My First Tool", with a menu bar.
//    begin("My First Tool", ::myToolActive, WindowFlag.MenuBar)
//    menuBar {
//        menu("File") {
//            menuItem("Open..", "Ctrl+O")) { /* Do stuff */ }
//            menuItem("Save", "Ctrl+S"))   { /* Do stuff */ }
//            menuItem("Close", "Ctrl+W"))  { myToolActive = false }
//        }
//    }
//
//// Edit a color (stored as FloatArray[4] or Vec4)
//    colorEdit4("Color", myColor);
//
//// Plot some values
//    val myValues = floatArrayOf( 0.2f, 0.1f, 1f, 0.5f, 0.9f, 2.2f )
//    plotLines("Frame Times", myValues)
//
//// Display contents in a scrolling region
//    textColored(Vec4(1,1,0,1), "Important Stuff")
//    withChild("Scrolling") {
//        for (int n = 0; n < 50; n++)
//        text("%04d: Some text", n)
//    }
//    end()
//}
