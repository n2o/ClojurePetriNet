# DSL Struktur
## Atom @nets

### Transitionen
*09.02.14*
Ursprüngliche Idee war es die Transitionen wie folgt zu gestalten:

```clojure
(defn add-edge-to-transition
  "Add an edge from a place to a transition."
  [net from to tokens]
  (swap! nets assoc-in [net :edges-to-trans from to] tokens))
```

Allerdings erwies sich das teilen in 'to' und 'tokens' als nicht so geschickt, da später beim ```(merge-net ...)``` die einzelnen Funktionen für ```(add-transition-to-trans ...)``` nicht verwendet werden konnten. Das lag daran, dass beim mergen neue Listen von Transitionen erstellt wurden, die nicht ganz mit der normalen Datenstruktur übereinstimmten.
Nun wurde die Funktion ein wenig abgeändert und es wird eine fertige Map von {Transitionen -> Tokens} übergeben:

```clojure
(defn add-edge-to-transition
  "Add an edge from a place to a transition."
  [net from to]
  (swap! nets assoc-in [net :edges-to-trans from] to))
```

So kann beim Mergen von Netzen mit einem einfachen Map alle Transitionen in ein neues Netz kopiert werden.


## Attribute NetAlive, TransitionAlive, NonEmpty, Not, Or
*09.02.14*



# GUI
## JavaFX vs. SeeSaw
*09.02.14*
Da JavaFX vielversprechend klingt und sich einiges vereinfacht haben soll, wollte ich mir JavaFX anschauen. Allerdings finde ich zur Benutzung in Clojure kaum etwas im Internet und als ich es geschafft habe mir selbst ein kleine Beispiel zusammen zu basteln, war die Interaktion mit der REPL quasi nicht gegeben (erst nach dem Schließen der GUI wurden die Befehle übertragen) und im Anschluss wurde die REPL gekillt -.- .

Nun versuche ich mich an SeeSaw.