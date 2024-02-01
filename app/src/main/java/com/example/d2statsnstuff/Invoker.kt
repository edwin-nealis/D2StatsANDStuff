package com.example.d2statsnstuff

import java.util.concurrent.Executor

class Invoker : Executor {
    override fun execute(runnable: Runnable) {
        runnable.run()
    }
}