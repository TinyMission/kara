package karatests.controllers

import org.apache.log4j.BasicConfigurator


/** Tests for dispatching routes to get action info. */
fun main(args : Array<String>) {
    BasicConfigurator.configure()

    runDispatchTests()
    runActionTests()
}