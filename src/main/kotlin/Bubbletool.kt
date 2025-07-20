import cwlib.enums.*
import cwlib.resources.RLevel
import cwlib.structs.things.Thing
import cwlib.structs.things.components.EggLink
import cwlib.structs.things.parts.*
import cwlib.types.SerializedResource
import cwlib.types.data.GUID
import cwlib.types.data.NetworkPlayerID
import cwlib.types.data.ResourceDescriptor
import cwlib.types.data.Revision
import cwlib.util.FileIO
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.cli.required
import org.joml.Matrix4f
import org.joml.Vector3f
import java.io.File


const val startx = -19162.5f
const val starty = 1819.7742f

fun createBubble(ref: GUID, x: Int, y: Int): Thing {
    val thing = Thing(level.nextUID)
    val translation = Vector3f(startx + x.toFloat() * 210, starty +y.toFloat() * 210, 0f)
    val pos = PPos(Matrix4f().identity().translate(translation))
    pos.thingOfWhichIAmABone = thing
    thing.setPart(Part.POS, pos)
    val body = PBody()
    thing.setPart(Part.BODY, body)
    val mesh = PRenderMesh(ResourceDescriptor(21180, ResourceType.MESH))
    mesh.boneThings = arrayOf(thing)
    thing.setPart(Part.RENDER_MESH, mesh)
    val trigger = PTrigger(TriggerType.RADIUS, 136f)
    thing.setPart(Part.TRIGGER, trigger)
    val script = PScript(ResourceDescriptor(27432, ResourceType.SCRIPT))
    thing.setPart(Part.SCRIPT, script)
    val shape = PShape(arrayOf(
        Vector3f(101.42221f, -27.175983f, -7.898791E-8f),
        Vector3f(90.93268f, -52.499985f, -7.898802E-8f),
        Vector3f(74.24622f, -74.2462f, -7.898757E-8f),
        Vector3f(52.500015f, -90.932655f, -7.898825E-8f),
        Vector3f(27.176016f, -101.42221f, -7.898825E-8f),
        Vector3f(1.3769088E-5f, -105.0f, -7.8987796E-8f),
        Vector3f(-27.175987f, -101.42221f, -7.898825E-8f),
        Vector3f(-52.499992f, -90.93268f, -7.898734E-8f),
        Vector3f(-74.24621f, -74.246216f, -7.8987796E-8f),
        Vector3f(-90.932655f, -52.500004f, -7.898802E-8f),
        Vector3f(-101.42221f, -27.176008f, -7.898802E-8f),
        Vector3f(-105.0f, -9.179392E-6f, -7.8987945E-8f),
        Vector3f(-101.42221f, 27.17599f, -7.898802E-8f),
        Vector3f(-90.93268f, 52.499996f, -7.898802E-8f),
        Vector3f(-74.246216f, 74.24621f, -7.8987796E-8f),
        Vector3f(-52.500004f, 90.93266f, -7.8987796E-8f),
        Vector3f(-27.176006f, 101.42221f, -7.8987796E-8f),
        Vector3f(-4.589696E-6f, 105.0f, -7.898825E-8f),
        Vector3f(27.175997f, 101.42221f, -7.8987796E-8f),
        Vector3f(52.499996f, 90.93268f, -7.898825E-8f),
        Vector3f(74.24621f, 74.24621f, -7.898757E-8f),
        Vector3f(90.93266f, 52.5f, -7.898802E-8f),
        Vector3f(101.42221f, 27.175999f, -7.898791E-8f),
        Vector3f(105.0f, 0.0f, -7.8987945E-8f),
    ))
    shape.material = ResourceDescriptor(17661, ResourceType.MATERIAL)
    shape.polygon.loops = intArrayOf(24)
    shape.thickness = 70.0f
    shape.massDepth = 1.0f
    thing.setPart(Part.SHAPE, shape)
    val gameplayData = PGameplayData()
    gameplayData.gameplayType = GameplayPartType.PRIZE_BUBBLE
    gameplayData.eggLink = EggLink(ResourceDescriptor(ref, ResourceType.PLAN))
    thing.setPart(Part.GAMEPLAY_DATA, gameplayData)
    val ref = PRef(ResourceDescriptor(31743, ResourceType.PLAN))
    thing.setPart(Part.REF, ref)
    val group = PGroup()
    group.planDescriptor = ResourceDescriptor(31743, ResourceType.PLAN)
    thing.setPart(Part.GROUP, group)
    return thing
}

lateinit var level: RLevel

fun parseSimpleFile(filePath: String): ArrayList<Long> {
    val output = arrayListOf<Long>()
    val text = File(filePath).readLines()
    for (line in text) {
        if (line.isEmpty()) continue
        if (line.startsWith("g"))
            output.add(line.substring(1).toLongOrNull() ?: continue)
        else
            output.add(line.trim().toLongOrNull() ?: continue)
    }
    return output
}

fun parseSourceFile(filePath: String): List<Long> {
    if (filePath.endsWith(".txt"))
        return parseSimpleFile(filePath)
    else
        throw IllegalArgumentException("File extension is unknown")
}

fun main(args: Array<String>) {
    val parser = ArgParser("bubbletool")
    val maxWidth by parser.option(
        ArgType.Int,
        shortName = "w",
        fullName = "width",
        description = "The max width of the grid of bubbles."
    ).default(3)
    val sourceFile by parser.option(
        ArgType.String,
        fullName = "source",
        shortName = "s",
        description = "The source file to use"
    ).required()
    val outFile by parser.option(
        ArgType.String,
        fullName = "out",
        shortName = "o",
        description = "The output file to use"
    )
    parser.parse(args)
    val bubbles = parseSourceFile(sourceFile)
    level = SerializedResource(object {}::class.java.getResourceAsStream("custom_level.bin")!!.readAllBytes()).loadResource<RLevel>(
    RLevel::class.java)
    for (i in 0..level.playerRecord.playerIDs.size - 1) {
        level.playerRecord.playerIDs[i] = NetworkPlayerID("")
    }
    val world = level.worldThing.getPart<PWorld>(Part.WORLD)
    val things = ArrayList<Thing>()
    var x = 0; var y = 0
    for (i in bubbles) {
        things.add(createBubble(GUID(i), x, y))
        x++
        if (x > maxWidth) {
            x = 0
            y++
        }
    }
    world.things.addAll(things)
    level.worldThing.setPart<PWorld>(Part.WORLD, world)
    level.worldThing.world = level.worldThing
    val data = level.build(Revision(626, Branch.LEERDAMMER.id.toInt(), Branch.LEERDAMMER.revision.toInt()), 7)
    FileIO.write(SerializedResource.compress(data), outFile)
}