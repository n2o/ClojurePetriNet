# Petri Net Documentation and Design Decisions

## Abstract

This project is about developing a petri net in the functional
language Clojure. In this document the decisions for the design
of the project are described. The main object was to stay simple
and functional.

## Architecture Decisions

For this project a generic basic approach was chosen to
encapsulate the program as follows:

* The **logic** of the program is implemented in `core.clj`. Without
  checking for invalid input all commands are executed on the DSL.
* On top of the logic there is the **API** in `api.clj`. Here are the
  functions which can be used by the simulator and the GUI.
* The **Simulator** in `simulator.clj` uses functions from the API to
  make (random) modification of the petri net possible.
* The **GUI** visualizes the current DSL.

## Why are there no private functions?

### (Partly) Test Driven Development

To be honest: In the beginning of such a project I believe there are mostly
no tests - just start hacking and see where it leads to. So it was in
this project.

But as complexity increased I thought that tests could be very useful.
I started to write some tests for the Controller before I started with
the merge function, because this was very hard and I did not want to
kill some functions. From this point I wrote additional tests *before*
implementing new features, as it is known in test driven development.

While testing my functions I had the problem that private functions
can not be accessed directly with *Midje*. I decided to make all
functions public until the development has finished.

### Midje

As stated above, Midje is used by this project. Midje offers a very simple
notation to add new facts and this is very very great

```clojure
(facts "Merging transitions from two nets."
  (fact
    (controller/merge-transitions :first #{:some-transitions} #{...} {...})
        => #{:result}
    (controller/merge-transitions :first #{:another-fact} #{:foo} {:bar})
        => #{:next-result}))
```

While the development an extra Terminal windows was running `lein midje :autotest`
so on every change in the project all facts are evaluated and you can see
the process.

#### Simple way to test

To give the test some sample input, the function `(init-nets)` can be found at the
bottom of the Controller. This needs to be uncommented to enable some input. The
tests will evaluate these inputs and show the results.

## Controller

#### Logic
All functions are here to initialize and manipulate the petri
nets are in the Controller. All other functions are using these
functions to manipulate the state of the program.

#### No inspection for valid input
To keep the functions simple, none of them will check any input.
They only do what has been typed into the REPL. So the liability
totally depends on the user's input. That is okay, because later
on will be an API available to manipulate the DSL including an
inspection for valid inputs.

#### Simplicity
Nearly every function was built simple. Only `merge-net` appears a
bit complected, but it just calls all the little merge-functions and
combines every result to get the new merged net.


### General structure of the database

#### Data Structure
I used one atom to store the whole database, because each net is
a big state containing places, transitions, their connections and the
properties. For the database was therefore a big hash-map chosen. 

By this way all petri nets are stored in the atom `nets`:

```clojure
{:net1 {:all :properties}
 :net2 {:some-other :properties}
 ...}
```

#### Edges from transition to places
Each net has an entry in it's map called `:edges-from-trans`. It has the 
following struture:

```clojure
;; read :edges-from-trans {:from {:to :costs}}
:edges-from-trans {:t1 {:p1 20}, :t2 {:p2 10, :p1 22}}
```

The edges have a map, which stores the transition `t1, t2` as a key
and the values are again maps containing the places `p1, p2` the transitions
have an edge to with their corresponding costs. Multiple edges from one transition
to different places can be stored into this structure.

#### Edges from place to transitions
For transitions from one specific place to multiple transitions have the 
same structure:

```clojure
;; read :edges-from-trans {:from {:to :costs}}
:edges-to-trans {:p1 {:t1 4}, :p2 {:t2 43, :t1 41}}
```

One place has a map whose keys are transitions and the values the costs 
for this edge.

#### Places
Places are stored in a map (maps are great!) the way that keys are the
places' names and the values are the number of tokens for each place:

```clojure
;; read :places {:place-name :tokens}
:places {:p1 21, :p2 42, :p3 100, :p4 44}
```

This structure provides easy access to the tokens for each place and
can be manipulated very simple.

#### Transitions
Transitions are stored in a set:

```clojure
:transitions #{:t1 :t2 :t3}
```

This structure is mainly needed for the GUI and the simulator to look
up very fast transitions, i.e. to choose a fireable transition and fire it
and to list all transitions in a listbox in the GUI.

#### Properties
Properties are stored as a whole function call in a vector. In this case
it does not matter if I choose a set, a vector or a quoted list, because 
the only thing the structure needs to provide is to store all calls without
evaluating them to early:

```clojure
:props [(petri-net.simulator/transition-alive? :net :transition)]
```

They can be stored into the vector by syntax quoting them.

Choosing this structure seems to be a good solution, because I do not loose
any information when executing it, because the whole call is stored. When a
user adds a property, the GUI will evaluate it in each step and this can be
executed as many times as wanted.


#### Sample net
Using all the steps above lead to the following structure:

```clojure
{:example-net
 {:edges-from-trans
    {:from-t {:to-p :cost}}
  :edges-to-trans
    {:from-p {:to-t1 1 :to-t2 1}}
  :places
    {:p1 0 :p2 1}
  :transitions
    #{:to-t1 :to-t1 :from-t}
  :props
    [(petri-net.simulator/net-alive? :example-net)]}

 :next-net
   {...}
}
```

Choosing this structure provides the great ability to use the
function `assoc-in`, which makes most of the work when the user
wants to create or modify one of the edges. The simple call

```clojure
(swap! nets assoc-in [net :edges-to-trans from to] tokens)
```

has two functionalities:

* Update existing edge
* Add new edge

And this is exactly that, what is needed for this structure: one function
to get deep into the hash map structure and update the values needed.

#### Manipulating State
Only few functions are needed to manipulate the DSL. Because there must
be a place where the database of petri nets is modified, the functions
are reduced to a minimum:
  * new / delete / copy / load-net
  * load-db
  * add-place
  * add-transition
  * add-edge-to-transition
  * add-edge-from-transition
  * add / delete-property
  * merge-net


## API

#### If possible return value, else nil

In the API are functions built in which check the user's input
and executes it if possible. If not, it will always return `nil`.

#### Built on top of the Controller

Every function needed to directly manipulate the DSL, is
provided by the API. So the user does not need to get into the
pure logic of this program to work with the petri nets.

The API was designed to move the verification of the user's
input into another place than the core. So this job could be
encapsulated from the core.


## Simulator

The simulator is totally built with the
provided API. So I can assume that the user's input is correct.

It provides some functions to analyse the nets, called
*properties*, like `net-alive?`, `transition-alive?` and
`non-empty?`.

I chose to place those properties into a separate file,
because they have another objective than the *API* and the
*Controller*. The simulator shall only provide
  * add / delete properties
  * fire the transitions for the next steps of the simulation.

### Design Decisions

Implementing the properties was very difficult, because I had
to choose the right place when the evaluation of them should
take place. First I thought evaluate them directly would be a
good idea, but then I have to store the result and the
property separate in two different stores. That is not elegant.

So I decided to store the whole function call into the `:props`
field of the net. The evaluation of these function calls is
done automatically on every step of the simulator and can be
viewed in the GUI. Although there are currently annoying
information about the namespaces of the functions in the
`:props` field, I think this is a very simple solution.

```clojure
{:props [(petri-net.simulator/net-alive? :example-net)]}
```
While loading the properties into the listbox in the GUI,
the properties are evaluated and the result is shown in the
corresponding listbox:

```clojure
true <= (petri-net.simulator/net-alive? :example-net)
```

### Not nil, but true or false

The properties shall always tell the user, if a value is `true`
or `false`. That is not really idiomatic, but is better for the
evaluation of the properties. The functions in the simulator
will then evaluate to true or false instead of `nil`.


## GUI

#### SeeSaw - GUIs done the Clojure way

To create a simple GUI there are all the known Java Tools which can be used
in Clojure, but this will not be the idiomatic way for it. So
I decided to use SeeSaw, which builts upon the known Java GUIs, but
uses more idiomatic functions to create the GUI in Clojure - and
it is very simple.

SeeSaw has no good documentation, which made it hard to find all
the arguments for most of the functions. But the simple way all
functions are implemented made it easy to guess how the arguments
are called and shall be used.

The GUI consists of three columns, while the one in the middle
is only used to create some space between the other two columns.
The user needs to create a new net into the database or load a
net / database from file (one database including three nets are
provided with this software) and needs to choose from the listboxes
what he wants to edit / fire / watch at in the nets.

### Listeners for Simplicity

To keep it simple there are several listeners on all textboxes,
which look up if anything is selected in the textbox. If so the
GUI activates some buttons, which can now be used. When the
selection was removed, the buttons are toggled.

So the user can only use functions which are really available.
This provides a better usability, because the user can always
see what he can now do with the simulator.

### Net Actions

Use the buttons in this field to add new stuff to the
selected net.

### Working with the Simulator

The Simulator has two functions:

  1. Select a transition, then select "Fire" to fire it
  2. Specify how many steps the simulator will randomly
     do for you

In the random mode the simulator looks which transitions are
fireable, chooses one and fires it. By this way it is ensured
that the number of steps the user chose is executed automatically
with high probability.

### Adding / Modifying Properties

The properties described above can be added by selecting some
elements from the listboxes. They are automatically combined,
added and evaluated by the simulator.

If the user selects one property, there is the possibility
to delete a property or surround it with *not*. The original
property is then deleted and a new one with `(not ...)` is
added to the list.

If there are more than one properties selected, the user can
combine the selected properties with *or*. The original
properties are then deleted and the new one with `(or ...)`
and the selected properties as arguments is added.


## Some other design decisions

### GUI

While looking for a GUI there are several ways which you can choose
for Clojure (thanks to Java interop). But as I tried to create a GUI
with JavaFX, there was nearly no possibility to get somehow a 
guide or even only some information to build a GUI using JavaFX.

After a while using SeeSaw seemed to be more idiomatic, simpler
and almost easier (although the lack of a good documentation).

### Add Edge to Transition

In the first implementation it was only possible to add one edge
to the existing net. That was the way to go until the evil merge
functions got implemented. While merging it was easier to merge
all of the Edges from both nets at once and so the return value
was a map with multiple entries, which should be added to the DSL
at once.
