import core.components.configuration.modelConfiguration

modelConfiguration {
    agentInterface("ModelSettings") {
        setter<Double>("hourSec")
        request<Unit>("Restart")
    }
    agentInterface("Car") {
        setter<Int>("speed")
        setter<Double>("restTime")
        setter<Double>("capacity")
        setter<Boolean>("workOnSchedule")
        setter<Int>("fuelConsumption")
    }
    agentInterface("Parking") { }
    agentInterface("Dump") { }
    agentInterface("Turn") { }
    agentInterface("GarbageCan") {
        setter<Double>("intensity")
        setter<Double>("capacity")
        setter<Double>("collectIntensity")
    }
}