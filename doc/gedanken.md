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

So können beim Mergen von Netzen mit einem einfachen Map alle Transitionen in ein neues Netz kopiert werden.


## Attribute NetAlive, TransitionAlive, NonEmpty, Not, Or
*09.02.14*
Die erste Idee ist, alle Attribute in die Datenbank @nets zum jeweiligen Netz zu speichern.


# GUI
## JavaFX vs. SeeSaw
*09.02.14*
Da JavaFX vielversprechend klingt und sich einiges vereinfacht haben soll, wollte ich mir JavaFX anschauen. Allerdings finde ich zur Benutzung in Clojure kaum etwas im Internet und als ich es geschafft habe mir selbst ein kleine Beispiel zusammen zu basteln, war die Interaktion mit der REPL quasi nicht gegeben (erst nach dem Schließen der GUI wurden die Befehle übertragen) und im Anschluss wurde die REPL gekillt -.- .

Mittlerweile glaube ich, dass das an der EMACS Cider REPL lag.

*17.02.14*
Seesaw ist super! In wenigen Schritten konnte zumindest das erste Fenster implementiert werden.

Für die GUI werde ich eine API schreiben, welche einfache Funktionen bereitstellt, die die GUI dann visuell darstellen kann. Dadurch trenne ich die Arbeit auf den Atomen in der GUI und liefere nur genau das, was benötigt wird.

# API
*17.02.14*
Arbeitet auf dem Core und liefert Daten aus den Atomen.

*18.02.14*

# Simulator
*18.02.14*
