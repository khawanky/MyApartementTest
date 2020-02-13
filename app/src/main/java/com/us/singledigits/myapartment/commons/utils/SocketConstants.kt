package com.us.singledigits.myapartment.commons.utils

enum class SocketConstants(val value: String) {

    EMIT_EMAIL("603a@a.com"),
    EMIT_USERNAME("residentiosapp"),
    EMIT_PASSWORD("resident"),
    EMIT_PORT("30876"),

    KEY_COMMAND("command"),
    KEY_DEVICE_TYPE("deviceType"),
    KEY_ACTION("action"),

    // Switches (Lights, Doors, Fans, Outlets)
    SWITCHES_COMMAND("updateLockFromAIAppVer1"),
    SWITCHES_TOGGLE_DEVICE_TYPE("switch"),
    SWITCHES_TOGGLE_ACTION_ON("on"),
    SWITCHES_TOGGLE_ACTION_OFF("off"),

    // Switches (Lights, Fans)
    SWITCHES_DIMMER_DEVICE_TYPE("dimmerswitch"),

    // Switches (Doors)
    DOORS_TOGGLE_DEVICE_TYPE("lock"),
    DOORS_TOGGLE_ACTION_LOCK("lock"),
    DOORS_TOGGLE_ACTION_UNLOCK("unlock"),

    // Thermostat
    THERMOSTAT_COMMAND("updateThermostatFromAIAppVer1"),
    THERMOSTAT_DEVICE_TYPE("thermostat"),
    THERMOSTAT_MODE_HEAT("heat"),
    THERMOSTAT_MODE_COOL("cool"),
    THERMOSTAT_MODE_OFF("off"),
    THERMOSTAT_SETPOINT_HEATING("heatingSetpoint"),
    THERMOSTAT_SETPOINT_COOLING("coolingSetpoint"),


    // IOT attributes
    IOT_ATTR_TYPE_SWITCH("switch"),
    IOT_ATTR_TYPE_LEVEL("level"),
    IOT_ATTR_TYPE_SWITCH_LEVEL("switchLevel"),
    IOT_ATTR_VALUE_SWITCH_ON("on"),
    IOT_ATTR_VALUE_SWITCH_OFF("off"),

    IOT_ATTR_TYPE_BATTERY("battery"),
    IOT_ATTR_TYPE_LOCK("lock"),
    IOT_ATTR_VALUE_LOCK_LOCKED("locked"),
    IOT_ATTR_VALUE_LOCK_UNLOCKED("unlocked"),


    IOT_ATTR_TYPE_TEMP("temperature"),
    IOT_ATTR_TYPE_THERMO_OP_STATE_CHANGED("thermostatOperatingStateChanged"),
    IOT_ATTR_TYPE_HEATING_SETPOINT("heatingSetpoint"),
    IOT_ATTR_TYPE_COOLING_SETPOINT("coolingSetpoint"),
    IOT_ATTR_TYPE_THERMO_SETPOINT("thermostatSetpoint"),
    IOT_ATTR_TYPE_THERMO_MODE("thermostatMode"),
    IOT_ATTR_TYPE_THERMO_FAN_MODE("thermostatFanMode"),
    IOT_ATTR_TYPE_THERMO_OP_STATE("thermostatOperatingState"),
    IOT_ATTR_VALUE_OP_STATE_CHANGED_COOLING("cooling"),
    IOT_ATTR_VALUE_THERMO_MODE_COOL("cool"),
    IOT_ATTR_VALUE_FAN_MODE_FANAUTO("fanAuto"),
    IOT_ATTR_VALUE_OP_STATE_COOLING("cooling"),


    //        IOT functions
    IOT_FUNCTION_LIGHT_TOGGLE("light-toggle"),
    IOT_FUNCTION_LIGHT_DIMMER("light-dimmer"),
    IOT_FUNCTION_FAN("fan"),
    IOT_FUNCTION_LOCK("lock"),
    IOT_FUNCTION_THERMOSTAT("thermostat"),
    IOT_FUNCTION_OUTLET("outlet")

}