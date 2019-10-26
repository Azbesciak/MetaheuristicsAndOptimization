package pl.poznan.put.mioib.benchmark

class ProgressBar(private val max: Int = 100) {
    private var state = -1

    fun update(progress: Double, iteration: Int, maxIterations: Int, time: Long, maxTime: Long) {
        val perc = (progress * max).toInt()
        if (state == perc) return
        state = perc
        var toPrint = "|"
        for (i in 0 until max) {
            toPrint += if (i <= perc + 1) "=" else " "
        }

        if (perc >= max)
            print("\r")
        else
            print("$toPrint|$perc% $iteration/$maxIterations | ${time / 1e6}/${maxTime / 1e6}ms\r")
    }
}
