import app.components.AgentInterface
import app.components.getSnapshot
import app.logger.Log
import app.logger.Logger
import app.services.model.control.AgentModelControlService
import app.services.scene.SceneService
import com.fasterxml.jackson.databind.ObjectMapper
import core.api.AgentModelApiClient
import core.api.dto.AgentSnapshot
import core.api.dto.Behaviour
import core.api.dto.GlobalArgs
import core.api.dto.Snapshot
import core.components.base.Property
import core.components.getComponent
import core.coroutines.AppContext
import core.services.AgentModelLifecycleEvent
import core.services.Event
import core.services.EventBus
import core.services.listen
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import java.time.Instant
import kotlin.coroutines.ContinuationInterceptor

//import Model.AgentSnapshots
//import Model.Snapshot
//import backend.core.components.VariablesComponentImpl
//import backend.core.entities.SceneMutableEntity
//import backend.core.scene.SceneImpl
//import com.google.flatbuffers.FlatBufferBuilder
//import com.google.flatbuffers.FlexBuffersBuilder
//import Series
//import mutableSeriesOf
//import Agent
//import core.entities.base.AgentPrototype
//import Scene
//import java.nio.ByteBuffer
//
//
//class ExampleAgentVariablesComponent : core.components.VariablesComponent() {
//    var name: String? = null
//        set(value) {
//            value!!
//            field = value
//            requests["setname"] = listOf(Value("value", String::class, value))
//        }
//        get() = _nameSeries.last
//
//    val nameSeries: Series<String>
//        get() = _nameSeries
//    private val _nameSeries = mutableSeriesOf<String>(10)
//
//    var x: Float? = null
//        set(value) {
//            value!!
//            field = value
//            requests["setx"] = listOf(Value("value", Float::class, value))
//        }
//        get() = _xSeries.last
//
//    val xSeries: Series<Float>
//        get() = _xSeries
//    private val _xSeries = mutableSeriesOf<Float>(10)
//
//    var y: Float? = null
//        private set
//
//    val ySeries: Series<Float>
//        get() = _ySeries
//    private val _ySeries = mutableSeriesOf<Float>(10)
//
//}
//
//class ExampleAgent(id: Int) : Agent(id)
//class ExampleAgentPrototype() : AgentPrototype()

@OptIn(ExperimentalUnsignedTypes::class)
fun main() {
//    val builder = FlatBufferBuilder(0)
//    val name = builder.createString("some name")
//    val responseText = builder.createString("some response");
//
//    val someActionResponse = Simple1Agent.SomeActionResponse.createSomeActionResponse(
//        builder, 45,
//        responseText
//    )
//    val response = Simple1Agent.Response.createResponse(
//        builder,
//        Simple1Agent.Responses.SomeActionResponse,
//        someActionResponse
//    )
//    val agentResponses = builder.createVectorOfTables(intArrayOf(response))
//
//
//
//    var builder2 = FlatBufferBuilder(0)
//    val simple1AgentSnapshot2 = Simple1Agent.Snapshot.createSnapshot(
//        builder2,
//        538,
//        name,
//        343455455,
//        344334,
//        agentResponses
//    )
//    builder2.finish(simple1AgentSnapshot2)
//
//    builder2 = FlatBufferBuilder(0)
//
//    val builder3 = FlatBufferBuilder(0)
//    val agentName = builder3.createString("name")
//    Simple1Agent.Snapshot.startSnapshot(builder3)
//    Simple1Agent.Snapshot.addId(builder3, 7987)
//    Simple1Agent.Snapshot.addName(builder3, agentName)
//    val simple1AgentSnapshot = Simple1Agent.Snapshot.endSnapshot(builder3)
//    builder3.finish(simple1AgentSnapshot)
//    val i = builder2.createByteVector(builder3.dataBuffer())
//    val agentSnapshot = Model.AgentSnapshot.createAgentSnapshot(
//        builder2,
//        AgentSnapshots.Simple1Agent_Snapshot,
//        i
//    )
//
//    val agentSnapshots = builder.createVectorOfTables(intArrayOf(agentSnapshot))
//
//
//    Snapshot.startSnapshot(builder)
//    Snapshot.addTime(builder, 1.1f)
//    Snapshot.addSnapshots(
//        builder, agentSnapshots
//    )
//    val orc = Snapshot.endSnapshot(builder)
//
//    builder.finish(orc)
//
//    val buf: ByteBuffer = builder.dataBuffer()
//    val monster = Snapshot.getRootAsSnapshot(buf)
//
//
//    println(monster.snapshots(0)?.value(Simple1Agent.Snapshot()))
//    val agentPrototype = ExampleAgentPrototype()
//    val varComp = ExampleAgentVariablesComponent()
//    val sceneMutableEntity = SceneMutableEntity(agentPrototype)
//    sceneMutableEntity.setComponent(varComp)
//
//    val scene = SceneImpl()
//    scene.addEntity(sceneMutableEntity, agentPrototype::class)
//
//    val immScene: Scene = scene
//    println()
//    val props = VariablesComponentImpl().props
//    props.get<Int>("dd")

//    val builder = FlexBuffersBuilder(FlexBuffersBuilder.BUILDER_FLAG_SHARE_KEYS_AND_STRINGS
//    )
//    val smap = builder.startMap()
//    val svec = builder.startVector()
//    builder.putInt(-100)
//    builder.putString("Fred")
//    builder.putFloat(4.0)
//    builder.endVector("vec", svec, false, false)
//    builder.putInt("foo", 100)
//    builder.endMap(null, smap)
//    val bb = builder.finish()
//
//    val root = FlexBuffers.getRoot(bb).asMap()
//    println()

    runBlocking {
        val dispatcher = coroutineContext[ContinuationInterceptor]
                as CoroutineDispatcher
        AppContext.context = dispatcher
    }

    val agentMOdelControlService = AgentModelControlService(object : AgentModelApiClient {
        override suspend fun run(globalArgs: GlobalArgs) {
            Logger.log("Request run", Log.Level.DEBUG)
            delay(1000)
            Logger.log("Requested run", Log.Level.DEBUG)
        }

        override suspend fun runAndSubscribeOnUpdate(globalArgs: GlobalArgs): Flow<Unit> {
            TODO("Not yet implemented")
        }

        override suspend fun callBehaviourFunctions(behaviour: Behaviour) {
            TODO("Not yet implemented")
        }

        var count = 0

        override suspend fun requestSnapshot(): Snapshot {
            Logger.log("Request snapshot", Log.Level.DEBUG)
            delay(1000)
            val snapshot = when (count) {
                0 -> Snapshot(
                    1f, mutableListOf(
                        AgentSnapshot(1, mutableMapOf("a" to 3, "b" to 7), mutableListOf()),
                        AgentSnapshot(2, mutableMapOf(), mutableListOf()),
                        AgentSnapshot(3, mutableMapOf(), mutableListOf())
                    ), mutableListOf()
                )
                1 -> Snapshot(
                    1f, mutableListOf(
                        AgentSnapshot(1, mutableMapOf(), mutableListOf()),
                        AgentSnapshot(2, mutableMapOf(), mutableListOf()),
                        AgentSnapshot(3, mutableMapOf(), mutableListOf()),
                        AgentSnapshot(4, mutableMapOf(), mutableListOf()),

                        ), mutableListOf()
                )
                2 -> Snapshot(
                    1f, mutableListOf(
                        AgentSnapshot(2, mutableMapOf(), mutableListOf()),
                        AgentSnapshot(3, mutableMapOf(), mutableListOf())
                    ), mutableListOf()
                )
                else -> Snapshot(
                    1f, mutableListOf(
                        AgentSnapshot(1, mutableMapOf(), mutableListOf()),
                        AgentSnapshot(56, mutableMapOf(), mutableListOf()),
                        AgentSnapshot(3, mutableMapOf(), mutableListOf())
                    ), mutableListOf()
                )
            }
            count++
            return snapshot
        }

        override suspend fun pause() {
            Logger.log("Request pause", Log.Level.DEBUG)
        }

        override suspend fun resume() {
            Logger.log("Request resume", Log.Level.DEBUG)
        }

        override suspend fun stop() {
            Logger.log("Request stop", Log.Level.DEBUG)
        }
    })

    EventBus.listen<Event>().subscribe { Logger.log(it.toString(), Log.Level.DEBUG) }
    val sceneService = SceneService()
    val services = listOf(agentMOdelControlService, sceneService)

    runBlocking(AppContext.context) {
        services.forEach { it.start() }
        agentMOdelControlService.run(5f)
        EventBus.listen<AgentModelLifecycleEvent.Update>().subscribe {
            Logger.log(sceneService.scene.agents.values.map {
                it.getComponent<AgentInterface>()!!.id
                val c = it.getComponent<AgentInterface>()
                val json = ObjectMapper().writeValueAsString(
                    Property(
                        "df",
                        Property::class.java,
                        Property("a", Int::class.java, 3)
                    )
                )
                val res = ObjectMapper().readValue(json, Property::class.java)
                Logger.log(
                    ObjectMapper().writeValueAsString(it.getComponent<AgentInterface>()!!.getSnapshot()).toString(),
                    Log.Level.DEBUG
                )
            }.toString(), Log.Level.DEBUG)
            Logger.log("start 10", Log.Level.DEBUG)
            val startTime = Instant.now().epochSecond
            while (Instant.now().epochSecond - startTime < 10) {

            }
            //delay(10000)
            Logger.log("end 10", Log.Level.DEBUG)
        }

        delay(1000000)

    }


}