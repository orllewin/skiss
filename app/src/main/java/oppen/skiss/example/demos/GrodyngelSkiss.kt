package oppen.skiss.example.demos

import android.graphics.Canvas
import oppen.skiss.lib.*
import oppen.skiss.lib.objects.Vektor

class GrodyngelSkiss (view: SkissView, private val onTouch: () -> Unit): Skiss(view) {

    private val background = color("#1d1d1d")
    private val motes = mutableListOf<Mote>()
    private val cycleOffspring = mutableListOf<Mote>()
    private val touchOffspring = mutableListOf<Mote>()
    private val maxRelationshipMemory = 20
    private val count = 100
    private var offspring = 0

    override fun setup(width: Int, height: Int) {

        fill(color("#efefef"))
        noStroke()
        noSmooth()

        motes.clear()
        repeat(count){ index ->
            motes.add(Mote(index))
        }
    }

    override fun update(canvas: Canvas) {
        background(background)

        motes.forEach { mote ->
            mote.update().draw()
        }

        motes.removeAll { mote ->
            !mote.alive
        }

        when (motes.size) {
            1 -> {
                repeat(count){ index ->
                    motes.add(Mote(index))
                }
            }
        }

        motes.addAll(cycleOffspring)
        cycleOffspring.clear()
        motes.addAll(touchOffspring)
        touchOffspring.clear()
    }

    override fun onTouch(x: Int, y: Int) {
        onTouch.invoke()
    }

    inner class Mote(private val id: Int, spawnLocation: Vektor?, private val parentId: Int?){

        constructor(id: Int) : this(id, null, null)

        private var location: Vektor = when {
            spawnLocation != null -> spawnLocation
            else -> Vektor(random(width), random(height))
        }

        private var velocity = Vektor(0f, 0f)
        private var acceleration: Vektor? = null
        private var maxSpeed = 2.5f
        private var relationshipLength = random(400, 900)
        private var allowedCycles = random(900, 2000).toInt()
        private var closestDistance = Float.MAX_VALUE
        private val exes = mutableListOf<Int>()
        private var currentCompanion = -1
        private var cycles = 0
        private var cyclesAttached = 0
        private var inRelationship = false
        private var hasOffspring = false
        var alive = true

        fun update(): Mote {
            if(motes.size == 1) return this
            cycles++
            if(cycles == allowedCycles){
                //die
                alive = false
            }

            //Get distances of all motes
            val distances = HashMap<Int, Float>()
            motes.forEach {mote ->
                val distance = location.distance(mote.location)
                distances[mote.id] = distance
            }

            //Sort - self will be index 0
            val sortedDistances = distances.toList().sortedBy { (_, value) -> value}

            //Closest neighbour
            val closestMote = motes.find { mote ->
                mote.id == sortedDistances[1].first

            }

            closestDistance = sortedDistances[1].second

            var directionToMote = closestMote!!.location - location
            directionToMote.normalize()

            //Is the closest mote the same as last cycle?
            if(currentCompanion == closestMote.id && closestDistance < 2.5){
                cyclesAttached++
            }

            inRelationship = cyclesAttached > 100


            //Update closest mote id after we've checked if they were previous closest
            currentCompanion = closestMote.id

            //Have they been together too long?
            when {
                cyclesAttached > relationshipLength -> {
                    exes.add(currentCompanion)
                    currentCompanion = -1
                    cyclesAttached = 0
                    relationshipLength = random(100, 500)
                    inRelationship = false
                }
            }

            //If closest neighbour is an ex then move away,
            //if it's a parent or siblings move away quickly,
            //otherwise stay close to current companion
            directionToMote *= when {
                exes.contains(closestMote.id) -> -0.6f
                (parentId != null && (parentId == closestMote.id)) -> -2.4f
                (parentId != null && (parentId == closestMote.parentId)) -> -2.4f
                else -> 0.2f
            }

            acceleration = directionToMote

            //Block only applies to motes in a relationship
            if(inRelationship){
                //Find nearest single mote, index 0 is self, index 1 is partner
                var foundThreat = false
                for(index in 2 until motes.size){
                    val mote = motes.find { mote ->
                        mote.id == sortedDistances[index].first
                    }

                    if(!mote!!.inRelationship && !foundThreat){
                        //Single - is a threat, move away
                        val directionToThreat = mote.location - location
                        directionToThreat.normalise()
                        acceleration = directionToMote + (directionToThreat * -0.4f)
                        foundThreat = true
                    }

                    if(foundThreat){
                        break
                    }
                }

                //Arbitary max population count
                if(!hasOffspring && cyclesAttached > relationshipLength/2 && motes.size < 140){
                    if(random(100) < 8){
                        hasOffspring = true
                        val numberOfOffspring = random(1, 2).toInt()
                        repeat(numberOfOffspring){
                            cycleOffspring.add(Mote(count + offspring, location, id))
                        }

                        offspring += numberOfOffspring
                    }
                }
            }

            velocity += acceleration!!

            if(inRelationship){
                val blackHole = Vektor(width/2, height/2)
                var directionToBlackHole = blackHole - location
                directionToBlackHole.normalize()
                directionToBlackHole *= 0.08f
                velocity += directionToBlackHole

                velocity.limit(maxSpeed / 1.25f)
            }else{
                velocity.limit(maxSpeed)
            }

            location += velocity

            if (exes.size == maxRelationshipMemory) exes.removeAt(0)//Forget oldest relationships

            return this
        }

        fun draw() {
            val diam = if(cycles < allowedCycles/2){
                map(cycles.toFloat(), 0f, allowedCycles.toFloat(), 1f, 40f)
            }else{
                map(cycles.toFloat(), 0f, allowedCycles.toFloat(), 40f, 1f)
            }
            checkBounds(diam)
            circle(location.x, location.y, diam)
        }

        private fun checkBounds(diam: Float) {
            when {
                location.x > width +diam -> location.x = -diam
                location.x < -diam -> location.x = width.toFloat() + diam
            }
            when {
                location.y > (height +diam) -> location.y = -diam
                location.y < -diam -> location.y = height.toFloat() + diam
            }
        }
    }
}