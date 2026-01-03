; Se crea un tipo de tortugas para nidos
breed [nidos nido]
; Se crean dos tipos de tortugas "ave1s" y "ave2s"
breed [ave2s ave2]
breed [ave1s ave1]

; Variables globales
globals [
    ; Energia que proporciona una fruta
    energia-fruta
    ; Costo energetico de volar un patch
    costo-vuelo
    ; Tasa metabolica diaria de las aves de dia
    tasa-metabolica-dia
    ; Tasa metabolica diaria de las aves de noche
    tasa-metabolica-noche
    ; Cantidad de aves sobrevivientes
    aves-sobrevivientes
    ; Booleano que indica si es noche o dia
    noche?
    ; Promedio de energia de las ave1s
    promedio-energia-ave1s
    ; Promedio de energia de las ave2s
    promedio-energia-ave2s
    ; Promedio de energia total de las aves
    promedio-energia-total
    ; Promedio de comida de los patches tipo 1
    promedio-comida-patches-1
    ; Promedio de comida de los patches tipo 2
    promedio-comida-patches-2
    ; Promedio de comida de los patches tipo 3
    promedio-comida-patches-3
    ; Promedio de comida de los patches tipo 4
    promedio-comida-patches-4
    ; Promedio de comida de todos los patches
    promedio-comida-patches
    ; Tick donde se alcanzo el maximo de promedio-energia-total
    tick-maximo-energia
    ; Valor maximo de promedio-energia-total alcanzado
    maximo-energia
    ; Booleano para saber si ya se han iniciado los ticks
    ticks-iniciados?
    ; Porcentaje de aves sobrevivientes
    porcentaje-aves-sobrevivientes
]

; Atributos de los patches
patches-own [
    ; Tipo del patch 1, 2, 3 o 4
    tipo
    ; Concentracion de alimento en el patch, 0%, 30%, 60% o 85% (dependiendo del tipo de patch)
    concentracion-alimento
    ; Cantidad de alimento en el patch
    cantidad-alimento
    ; Indica si el patch es un nido o no
    es-nido?
    ; Variables para pruebas
    ;r-i-valor ; Variable para la prueba de decidir-nido
    ;p-i-valor ; Variable para la prueba de decidir-movimiento
]

; Atributos de las aves
ave1s-own [
    ; Energia del ave
    energia
    ; Rango de vision del ave
    vision
    ; Patch del nido anterior
    nido-anterior
    ; Peso del ave
    peso
]
ave2s-own [
    ; Energia del ave
    energia
    ; Rango de vision del ave
    vision
    ; Patch del nido anterior
    nido-anterior
    ; Peso del ave
    peso
]

; Funcion setup
to setup
    clear-all
    ; Asignar tamanio del mundo
    resize-world 0 30 0 30
    set-patch-size 16
    __change-topology false false
    ; Cada fruta proporcina 3 kcal
    set energia-fruta 3
    ; Cada ave tiene una tasa metabolica diaria de 8.167 kcal
    set costo-vuelo 8.167
    ; Cada ave de dia tiene una tasa metabolica diaria de 1.11536 kcal por 30 minutos
    set tasa-metabolica-dia 1.11536
    ; Cada ave de noche tiene una tasa metabolica diaria de (0.003644256 * peso) kcal por 30 minutos
    set tasa-metabolica-noche 0.003644256
    ; Inicializar patches
    setup-patches
    ; Inicializar aves
    setup-aves
    ; Actualizar cantidad de aves sobrevivientes
    actualizar-sobrevivientes
    ; Actualizar promedios
    set tick-maximo-energia 0
    set maximo-energia 0
    set ticks-iniciados? false
    actualizar-promedios
    ; Actualizar si es noche o dia
    reset-ticks
    set ticks-iniciados? true
    actualizar-noche-dia
end

; Funcion para inicializar los patches
to setup-patches
    ; Asignar tipos a los patches
    ; Tipo 1: concentracion de alimento 0% y probabilidad de 15%
    ; Tipo 2: concentracion de alimento 30% y probabilidad de 25%
    ; Tipo 3: concentracion de alimento 60% y probabilidad de 30%
    ; Tipo 4: concentracion de alimento 85% y probabilidad de 30%
    ask patches [
        let probabilidad random 100
        ; Tipo 1
        if probabilidad < 15 [
            set tipo 1
            set concentracion-alimento 0
            set pcolor 69.9
        ]
        ; Tipo 2
        if probabilidad >= 15 and probabilidad < 40 [
            set tipo 2
            set concentracion-alimento 30
            set pcolor 69
        ]
        ; Tipo 3
        if probabilidad >= 40 and probabilidad < 70 [
            set tipo 3
            set concentracion-alimento 60
            set pcolor 68
        ]
        ; Tipo 4
        if probabilidad >= 70 [
            set tipo 4
            set concentracion-alimento 85
            set pcolor 67
        ]
        ; Asignar cantidad de alimento, formula F-i = max([10 X A], 0), con X ∼ N(c, 100)
        let X random-normal concentracion-alimento 10
        let A (porcentaje-abundancia / 100)
        set cantidad-alimento max (list round (10 * X * A) 0)
    ]
    ; Crear patches nido
    ask patches [set es-nido? false]
    while [count patches with [es-nido? = true] < numero-nidos] [
        ask one-of patches with [es-nido? = false] [
            set es-nido? true
            sprout-nidos 1 [
                set shape "circle"
                set color brown
                set size 0.7
            ]
        ]
    ]
end

; Funcion para inicializar las aves
to setup-aves
    ; Crear aves tipo 1
    create-ave1s cantidad-ave1 [
        set shape "bird side" ; se necesita importar esta forma desde la libreria de formas
        set color sky
        set size 0.9
        set energia 200
        set vision 3
        move-to one-of nidos
        set nido-anterior patch-here
        set peso random-normal 30.0 5.0
    ]
    ; Crear aves tipo 2
    create-ave2s cantidad-ave2 [
        set shape "bird side" ; se necesita importar esta forma desde la libreria de formas
        set color orange
        set size 1.4
        set energia 300
        set vision 5
        move-to one-of nidos
        set nido-anterior patch-here
        set peso random-normal 77.0808 5.0
    ]
end

; Funcion go
to go
    ; Si no es invierno, detener simulacion
    if not es-inverno? [
        show "el invierno ha terminado, la simulación se detiene"
        show (word "aves sobrevivientes " aves-sobrevivientes)
        show (word "tick maximo energia promedio " tick-maximo-energia ", maximo energia promedio " maximo-energia)
        ; Calcular porcentaje de aves sobrevivientes
        let cantidad-aves-inicial cantidad-ave1 + cantidad-ave2
        set porcentaje-aves-sobrevivientes (aves-sobrevivientes / cantidad-aves-inicial) * 100
        show (word "porcentaje de aves sobrevivientes: " porcentaje-aves-sobrevivientes "%")
        stop
    ]
    ; Si no hay aves sobrevivientes, detener simulacion
    if not hay-aves-sobrevivientes? [
        show "no hay aves sobrevivientes, la simulación se detiene"
        show (word "tick maximo energia promedio " tick-maximo-energia ", maximo energia promedio " maximo-energia)
        ; Calcular porcentaje de aves sobrevivientes
        let cantidad-aves-inicial cantidad-ave1 + cantidad-ave2
        set porcentaje-aves-sobrevivientes (aves-sobrevivientes / cantidad-aves-inicial) * 100
        show (word "porcentaje de aves sobrevivientes: " porcentaje-aves-sobrevivientes "%")
        stop
    ]
    ; Rutina de ave
    accion-aves
    ; Actualizar cantidad de aves sobrevivientes
    actualizar-sobrevivientes
    ; Actualizar promedios
    actualizar-promedios
    ; Actualizar si es noche o dia
    tick
    actualizar-noche-dia
end

; Funcion para actualizar si es noche o dia
to actualizar-noche-dia
    ; Cada tick representa 30 minutos
    ; Noche es de 21:00 a 5:59
    ; Dia es de 6:00 a 20:59
    let total-minutos ticks * 30
    let hora floor ((total-minutos / 60)) mod 24
    let minuto total-minutos mod 60
    ifelse (hora >= 21) or (hora <= 5)
    [set noche? true]
    [set noche? false]
end

; Funcion para saber si es invierno
to-report es-inverno?
    ; Cada tick representa 30 minutos
    ; El invierno dura 90 dias
    ; 90 dias * 24 horas/dia * 2 ticks/hora = 4320 ticks
    if ticks < 4320 [
        report true
    ]
    report false
end

; Funcion para actualizar la cantidad de aves sobrevivientes
to actualizar-sobrevivientes
    set aves-sobrevivientes (count ave1s + count ave2s)
end

; Funcion para saber si hay aves sobrevivientes
to-report hay-aves-sobrevivientes?
    ifelse aves-sobrevivientes > 0
    [report true]
    [report false]
end

; Funcion para la rutina de las aves
to accion-aves
    ask (turtle-set ave1s ave2s) [
        comportamiento-ave
    ]
end

; Funcion para el comportamiento de las aves
to comportamiento-ave
    ; Si no tiene energia, se muere
    if not tiene-energia? [morir]
    ; Revisar si es de noche o de dia
    ifelse (es-noche?)
    ; Si es de noche
    [
        ifelse (en-nido?)
        ; Si esta en un nido
        [
            ; Dormir
            dormir
            ; Gastar energia mientras duerme
            gastar-energia-dormida
        ]
        [
            ; Decidir a que nido volar
            let nuevo-nido decidir-nido
            ; Volar hacia el nido anterior
            volar nuevo-nido
            ; Si no tiene energia, se muere
            if not tiene-energia? [morir]
            ; Gastar energia mientras esta despierta
            gastar-energia-despierta
        ]
    ]
    ; Si es de dia
    [
        ; Decidir movimiento
        let destino decidir-movimiento
        ifelse destino = nobody
        ; Si elegio vuelo aleatorio, hacer vuelo aleatorio
        [
            vuelo-aleatorio
        ]
        [
            ifelse destino != patch-here
            ; Si elegio volar a un patch diferente, volar
            [volar destino]
            ; Si elegio quedarse en el patch actual, no hacer nada
            []
        ]
        ; Si no tiene energia, se muere
        if not tiene-energia? [morir]
        ; Decidir cuanto comer
        let cantidad-comer decidir-cantidad-comer
        ; Comer
        comer cantidad-comer
        ; Gastar energia mientras esta despierta
        gastar-energia-despierta
    ]
end

; Funcion para revisar si un ave tiene energia
to-report tiene-energia?
    if energia > 0 [
        report true
    ]
    report false
end

; Funcion para hacer que un ave muera
to morir
    die
end

; Funcion para saber si es de noche
to-report es-noche?
    report noche?
end

; Funcion para saber si un ave esta en un nido
to-report en-nido?
    ifelse [es-nido?] of patch-here
    [report true]
    [report false]
end

; Funcion para hacer que un ave duerma
to dormir
    ; Actualizar nido anterior
    set nido-anterior patch-here
end

; Funcion para gastar energia mientras el ave duerme
to gastar-energia-dormida
    ; Energia gastada = tasa metabolica en la noche * peso
    let energia-gastada max list (tasa-metabolica-noche * peso) 0
    set energia max list (energia - energia-gastada) 0
end

; Funcion para gastar energia mientras el ave esta despierta
to gastar-energia-despierta
    ; Energia gastada = tasa metabolica en el dia
    set energia max list (energia - tasa-metabolica-dia) 0
end

; Funcion para decidir a que nido volar
to-report decidir-nido
    ; Obtenemos los nidos dentro del rango de vision
    let nidos-vision patches in-radius vision with [es-nido? = true]
    ifelse (count nidos-vision = 0)
    ; Si no hay nidos en el rango de vision
    [
        ; Decidir volar al nido anterior
        report nido-anterior
    ]
    ; Si hay nidos en el rango de vision
    [
        ; Calcular la distancia al nido anterior
        let d-home distance nido-anterior
        ; Calcular r-i para cada nido y obtener el mejor r-i, formula r-i = max((d-home - d-i)/(d-home + d-i), 0)
        let nido-seleccionado nobody
        let r-i-seleccionado -1
        ask nidos-vision [
            let d-i distance myself
            let r-i max (list ((d-home - d-i) / (d-home + d-i)) 0)
            ask myself [
                if r-i > r-i-seleccionado [
                    set r-i-seleccionado r-i
                    set nido-seleccionado myself
                ]
            ]
            ;set r-i-valor r-i ; Parte para la prueba de decidir-nido
        ]
        ; Ver si el ave decide volar a ese nido o al nido anterior
        ifelse (random-float 1 < r-i-seleccionado)
        ; Si decide volar al nido seleccionado
        [
            report nido-seleccionado
        ]
        ; Si decide volar al nido anterior
        [
            report nido-anterior
        ]
    ]
end

; Funcion para volar a un patch
to volar [destino]
    if destino = nobody [stop]
    ; Energia gastada = costo de vuelo * patches de distancia
    let distancia distance destino
    let energia-gastada max list (costo-vuelo * distancia) 0
    set energia max list (energia - energia-gastada) 0
    ; Volar al patch destino
    move-to destino
end

; Funcion para decidir que hacer durante el dia
to-report decidir-movimiento
    ; Obtenemos los patches dentro del rango de vision, ignorando el patch actual
    let patch-actual patch-here
    let posibles-destinos patches in-radius vision with [self != patch-actual]
    ; Obtener la cantidad de comida en el patch actual F-0
    let f-0 [cantidad-alimento] of patch-here
    ; Calcular p-i para cada patch y obtener el mejor p-i, formula p-i = p_i = (25 F-i/(F-0 + 0.001) − 0.8167 d-i) / (25 F-i/(F-0 + 0.001) + 0.8167 d-i + 0.001)
    let patch-seleccionado nobody
    let p-i-seleccionado -1
    ask posibles-destinos [
        let d-i distance myself
        let f-i [cantidad-alimento] of self; TODO revisar si es correcto
        let parte1-i (25 * f-i) / (f-0 + 0.001)
        let parte2-i 0.8167 * d-i
        let numerador-i (parte1-i - parte2-i)
        let denominador-i (parte1-i + parte2-i + 0.001)
        let p-i numerador-i / denominador-i
        ask myself [
            if p-i > p-i-seleccionado [
                set p-i-seleccionado p-i
                set patch-seleccionado myself
            ]
        ]
        ;set p-i-valor p-i ; Parte para la prueba de decidir-movimiento
    ]
    ; Si el patch actual tiene mas comida o igual que el patch seleccionado
    if (f-0 >= [cantidad-alimento] of patch-seleccionado)
    [
        ; Actualizar el patch seleccionado
        set patch-seleccionado patch-here
        set p-i-seleccionado 1
        ; Si el patch actual tiene poca comida y decide hacer vuelo aleatorio
        if (f-0 < 50 and random-float 1 < 0.5) [set patch-seleccionado nobody]
    ]
    ; Ver si el ave decide volar al patch seleccionado
    ifelse (random-float 1 < p-i-seleccionado)
    ; Si decide volar al patch seleccionado
    [report patch-seleccionado]
    ; Si no decide volar al patch seleccionado
    [report patch-here]
end

; Funcion para hacer un vuelo aleatorio
to vuelo-aleatorio
    ; Elegir un angulo aleatorio
    let angulo random 360
    ; Elegir una distancia
    let distancia max list (random-normal vision 1) 0
    set distancia round distancia
    ; Calcular el patch destino
    let d-x distancia * cos angulo
    let d-y distancia * sin angulo
    let destino patch-at d-x d-y
    ; Si no existe el patch destino, no realizar el vuelo aleatorio
    if destino = nobody [stop]
    ; Volar al patch destino
    volar destino
end

; Funcion para decidir cuanto comer
to-report decidir-cantidad-comer
    ; Obtenemos la cantidad de comida en el patch actual F-0
    let f-0 [cantidad-alimento] of patch-here
    ; Calcular c, formula c = max((5 F-0 / (400 + F-0) + ɛ), 0), con ɛ ∼ N(0, 4)
    let parte1 (5 * f-0) / (400 + f-0)
    let epsilon random-normal 0 2
    let c max list (parte1 + epsilon) 0
    report c
end

; Funcion para comer una cantidad de comida
to comer [cantidad]
    ; Calcular cuanta comida se puede comer
    let comida-disponible [cantidad-alimento] of patch-here
    let comida-comer min list cantidad comida-disponible
    ; Disminuir la cantidad de comida en el patch
    ask patch-here [set cantidad-alimento max list (cantidad-alimento - comida-comer) 0]
    ; Aumentar la energia del ave
    set energia energia + (comida-comer * energia-fruta)
end

; Funcion para reportar el dia y hora
to-report tiempo
    let total-minutos ticks * 30
    let dia floor (ticks / 48) + 1
    let hora floor ((total-minutos / 60)) mod 24
    let minuto total-minutos mod 60
    report (word "Dia " dia " — Hora " (ifelse-value (hora < 10) [word "0" hora] [hora]) ":" (ifelse-value (minuto < 10) [word "0" minuto] [minuto]))
end

; Funcion para reportar si es noche o dia
to-report noche-dia
    ifelse noche?
    [report "Noche ⏾"]
    [report "Dia ☀"]
end

; Funcion para actualizar los promedios
to actualizar-promedios
    ; Promedio de energia de las ave1s
    let energia-total-ave1s sum [energia] of ave1s
    ifelse cantidad-ave1 > 0
    [set promedio-energia-ave1s energia-total-ave1s / cantidad-ave1]
    [set promedio-energia-ave1s 0]
    ; Promedio de energia de las ave2s
    let energia-total-ave2s sum [energia] of ave2s
    ifelse cantidad-ave2 > 0
    [set promedio-energia-ave2s energia-total-ave2s / cantidad-ave2]
    [set promedio-energia-ave2s 0]
    ; Promedio de energia total de las aves
    let energia-total-aves energia-total-ave1s + energia-total-ave2s
    let cantidad-aves cantidad-ave1 + cantidad-ave2
    ifelse cantidad-aves > 0
    [set promedio-energia-total energia-total-aves / cantidad-aves]
    [set promedio-energia-total 0]
    ; Promedio de comida de los patches tipo 1
    set promedio-comida-patches-1 mean [cantidad-alimento] of patches with [tipo = 1]
    ; Promedio de comida de los patches tipo 2
    set promedio-comida-patches-2 mean [cantidad-alimento] of patches with [tipo = 2]
    ; Promedio de comida de los patches tipo 3
    set promedio-comida-patches-3 mean [cantidad-alimento] of patches with [tipo = 3]
    ; Promedio de comida de los patches tipo 4
    set promedio-comida-patches-4 mean [cantidad-alimento] of patches with [tipo = 4]
    ; Promedio de comida de todos los patches
    set promedio-comida-patches mean [cantidad-alimento] of patches
    ; Actualizar tick-maximo-energia y maximo-energia
    if promedio-energia-total > maximo-energia [
        set maximo-energia promedio-energia-total
        ; Si ya se se realizo reset-ticks, actualizar tick-maximo-energia
        if ticks-iniciados? [set tick-maximo-energia ticks]
    ]
end

; Funciones setup y go para las pruebas

; Prueba de decidir-nido
; Se recomienda crear un monitor [r-i-valor] of patch mouse-xcor mouse-ycor
; Para realizar la prueba se tiene que dar click en setup2 y luego en go2 una sola vez, para volvar a usar go2 se tiene que volver a dar click en setup2
; La funcion go2 no puede ser forever
; Funcion setup para la prueba de decidir-nido
;to setup2
;    clear-all
;    resize-world -4 4 -4 4
;    set-patch-size 30
;    __change-topology false false
;    set costo-vuelo 8.167
;    ask patches [
;        set es-nido? false
;        if random-float 1 < 0.2 [
;            set pcolor yellow
;            set es-nido? true
;        ]
;    ]
;    create-ave1s 1 [
;        set shape "bird side"
;        set color sky
;        set size 1.4
;        set energia 300
;        set vision 5
;        move-to one-of patches with [es-nido? = false]
;        set nido-anterior one-of patches with [es-nido? = true]
;        ask patch-here [set pcolor blue]
;        ask nido-anterior [set pcolor green]
;        set peso random-normal 77.0808 5.0
;    ]
;    reset-ticks
;end
; Funcion go para la prueba de decidir-nido
;to go2
;    ask ave1s [
;        let nido-s (decidir-nido)
;        show nido-s
;        show patch-here
;        if nido-s != nobody [
;            ask nido-s [set pcolor gray]
;            volar nido-s
;            set nido-anterior nido-s
;        ]
;    ]
;    tick
;end

; Prueba de decidir-movimiento
; Se recomienda crear un monitor [p-i-valor] of patch mouse-xcor mouse-ycor y otro monitor [cantidad-alimento] of patch mouse-xcor mouse-ycor
; Para realizar la prueba se tiene que dar click en setup3 y luego en go3 una sola vez, para volvar a usar go3 se tiene que volver a dar click en setup3
; La funcion go3 no puede ser forever
; Funcion setup para la prueba de decidir-movimiento
;to setup3
;    clear-all
;    resize-world -4 4 -4 4
;    set-patch-size 30
;    __change-topology false false
;    set costo-vuelo 8.167
;    ask patches [
;        set es-nido? false
;        if random-float 1 < 0.3 [
;        set pcolor yellow
;        set es-nido? true
;        ]
;        set p-i-valor 0
;    ]
;    create-ave1s 1 [
;        set shape "bird side"
;        set color sky
;        set size 1.4
;        set energia 300
;        set vision 10
;        move-to patch 0 0
;        set nido-anterior one-of patches with [es-nido? = true]
;        ask nido-anterior [set pcolor green]
;        set peso random-normal 77.0808 5.0
;    ]
;    ask patches [
;        let probabilidad random 100
;        if probabilidad < 25 [
;            set tipo 1
;            set concentracion-alimento 0
;            set pcolor 69.9
;        ]
;        if probabilidad >= 25 and probabilidad < 50 [
;            set tipo 2
;            set concentracion-alimento 30
;            set pcolor 69
;        ]
;        if probabilidad >= 50 and probabilidad < 75 [
;            set tipo 3
;            set concentracion-alimento 60
;            set pcolor 68
;        ]
;        if probabilidad >= 75 [
;            set tipo 4
;            set concentracion-alimento 85
;            set pcolor 67
;        ]
;        let X random-normal concentracion-alimento 10
;        let A (porcentaje-abundancia / 100)
;        set cantidad-alimento max (list round (10 * X * A) 0)
;    ]
;    reset-ticks
;end
; Funcion go para la prueba de decidir-movimiento
;to go3
;    ask ave1s [
;        ask patches in-radius 2 [set cantidad-alimento 10]
;        let objetivo (decidir-movimiento)
;        if objetivo = nobody [
;            show "vuelo aleatorio"
;            stop
;        ]
;        show objetivo
;        show patch-here
;        ask objetivo [set pcolor red]
;    ]
;    tick
;end

; Prueba de vuelo aleatorio
; Para realizar la prueba se tiene que dar click en setup4 y luego en go4
; La funcion go4 no puede ser forever
; Funcion setup para la prueba de vuelo aleatorio
;to setup4
;    clear-all
;    resize-world -4 4 -4 4
;    set-patch-size 30
;    __change-topology false false
;    set costo-vuelo 8.167
;    ask patches [
;        set es-nido? false
;        if random-float 1 < 0.3 [
;            set pcolor yellow
;            set es-nido? true
;        ]
;    ]
;    create-ave1s 1 [
;        set shape "bird side"
;        set color sky
;        set size 1.4
;        set energia 300
;        set vision 3
;        move-to patch 0 0
;        set nido-anterior one-of patches with [es-nido? = true]
;        ask nido-anterior [set pcolor green]
;        set peso random-normal 77.0808 5.0
;    ]
;    reset-ticks
;end
; Funcion go para la prueba de vuelo aleatorio
;to go4
;    ask ave1s [vuelo-aleatorio]
;    tick
;end

; Prueba de decidir-cantidad-comer y comer
; Para realizar la prueba se tiene que dar click en setup5 y luego en go5
; La funcion go5 no puede ser forever
; Funcion setup para la prueba de decidir-cantidad-comer y comer
;to setup5
;    clear-all
;    resize-world -4 4 -4 4
;    set-patch-size 30
;    __change-topology false false
;    set costo-vuelo 8.167
;    ask patches [
;        set es-nido? false
;        if random-float 1 < 0.3 [
;            set pcolor yellow
;            set es-nido? true
;        ]
;        set cantidad-alimento 50
;    ]
;    create-ave1s 1 [
;        set shape "bird side"
;        set color sky
;        set size 1.4
;        set energia 300
;        set vision 3
;        move-to patch 0 0
;        set nido-anterior one-of patches with [es-nido? = true]
;        ask nido-anterior [set pcolor green]
;        set peso random-normal 77.0808 5.0
;    ]
;    ask patches [
;        let probabilidad random 100
;        if probabilidad < 25 [
;            set tipo 1
;            set concentracion-alimento 0
;            set pcolor 69.9
;        ]
;        if probabilidad >= 25 and probabilidad < 50 [
;            set tipo 2
;            set concentracion-alimento 30
;            set pcolor 69
;        ]
;        if probabilidad >= 50 and probabilidad < 75 [
;            set tipo 3
;            set concentracion-alimento 60
;            set pcolor 68
;        ]
;        if probabilidad >= 75 [
;            set tipo 4
;            set concentracion-alimento 85
;            set pcolor 67
;        ]
;        let X random-normal concentracion-alimento 10
;        let A (porcentaje-abundancia / 100)
;        set cantidad-alimento max (list round (10 * X * A) 0)
;    ]
;    reset-ticks
;end
; Funcion go para la prueba de decidir-cantidad-comer y comer
;to go5
;    ask ave1s [
;        let cantidad-comer decidir-cantidad-comer
;        show (word "Cantidad a comer: " cantidad-comer)
;        let comida-disponible [cantidad-alimento] of patch-here
;        comer cantidad-comer
;        let comida-despues [cantidad-alimento] of patch-here
;        show (word "Comida disponible: " comida-disponible ", Comida despues de comer: " comida-despues)
;        show (word "Comida realmente comida: " (comida-disponible - comida-despues))
;    ]
;    tick
;end
@#$#@#$#@
GRAPHICS-WINDOW
220
63
724
568
-1
-1
16.0
1
10
1
1
1
0
0
0
1
0
30
0
30
0
0
1
ticks
30.0

BUTTON
5
173
68
206
NIL
setup
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL
1

SLIDER
4
89
176
122
numero-nidos
numero-nidos
1
100
77.0
1
1
NIL
HORIZONTAL

SLIDER
4
130
198
163
porcentaje-abundancia
porcentaje-abundancia
0
100
81.0
1
1
%
HORIZONTAL

SLIDER
5
10
177
43
cantidad-ave1
cantidad-ave1
1
50
22.0
1
1
NIL
HORIZONTAL

SLIDER
4
50
176
83
cantidad-ave2
cantidad-ave2
1
50
18.0
1
1
NIL
HORIZONTAL

BUTTON
74
173
137
206
NIL
go
T
1
T
OBSERVER
NIL
NIL
NIL
NIL
1

MONITOR
361
10
484
55
NIL
tiempo
17
1
11

MONITOR
491
10
561
55
NIL
noche-dia
17
1
11

BUTTON
143
174
206
207
NIL
go
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL
1

PLOT
738
10
1033
191
aves sobrevivientes
ticks
aves
0.0
10.0
0.0
10.0
true
true
"" ""
PENS
"ave1" 1.0 0 -13791810 true "" "plot count ave1s"
"ave2" 1.0 0 -955883 true "" "plot count ave2s"
"total" 1.0 0 -5204280 true "" "plot aves-sobrevivientes"

PLOT
738
201
1033
360
energia promedio
tick
kcal
0.0
10.0
0.0
10.0
true
true
"" ""
PENS
"ave1" 1.0 0 -13791810 true "" "plot promedio-energia-ave1s\n"
"ave2" 1.0 0 -955883 true "" "plot promedio-energia-ave2s\n"
"total" 1.0 0 -5204280 true "" "plot promedio-energia-total"

PLOT
738
370
1034
531
alimento promedio en patches
ticks
comida
0.0
10.0
0.0
10.0
true
true
"" ""
PENS
"tipo1" 1.0 0 -3889007 true "" "plot promedio-comida-patches-1"
"tipo2" 1.0 0 -1604481 true "" "plot promedio-comida-patches-2"
"tipo3" 1.0 0 -1264960 true "" "plot promedio-comida-patches-3"
"tipo4" 1.0 0 -6759204 true "" "plot promedio-comida-patches-4"
"total" 1.0 0 -8330359 true "" "plot promedio-comida-patches"

@#$#@#$#@
## WHAT IS IT?

(a general understanding of what the model is trying to show or explain)

## HOW IT WORKS

(what rules the agents use to create the overall behavior of the model)

## HOW TO USE IT

(how to use the model, including a description of each of the items in the Interface tab)

## THINGS TO NOTICE

(suggested things for the user to notice while running the model)

## THINGS TO TRY

(suggested things for the user to try to do (move sliders, switches, etc.) with the model)

## EXTENDING THE MODEL

(suggested things to add or change in the Code tab to make the model more complicated, detailed, accurate, etc.)

## NETLOGO FEATURES

(interesting or unusual features of NetLogo that the model uses, particularly in the Code tab; or where workarounds were needed for missing features)

## RELATED MODELS

(models in the NetLogo Models Library and elsewhere which are of related interest)

## CREDITS AND REFERENCES

(a reference to the model's URL on the web if it has one, as well as any other necessary credits, citations, and links)
@#$#@#$#@
default
true
0
Polygon -7500403 true true 150 5 40 250 150 205 260 250

airplane
true
0
Polygon -7500403 true true 150 0 135 15 120 60 120 105 15 165 15 195 120 180 135 240 105 270 120 285 150 270 180 285 210 270 165 240 180 180 285 195 285 165 180 105 180 60 165 15

arrow
true
0
Polygon -7500403 true true 150 0 0 150 105 150 105 293 195 293 195 150 300 150

bird side
false
0
Polygon -7500403 true true 0 120 45 90 75 90 105 120 150 120 240 135 285 120 285 135 300 150 240 150 195 165 255 195 210 195 150 210 90 195 60 180 45 135
Circle -16777216 true false 38 98 14

box
false
0
Polygon -7500403 true true 150 285 285 225 285 75 150 135
Polygon -7500403 true true 150 135 15 75 150 15 285 75
Polygon -7500403 true true 15 75 15 225 150 285 150 135
Line -16777216 false 150 285 150 135
Line -16777216 false 150 135 15 75
Line -16777216 false 150 135 285 75

bug
true
0
Circle -7500403 true true 96 182 108
Circle -7500403 true true 110 127 80
Circle -7500403 true true 110 75 80
Line -7500403 true 150 100 80 30
Line -7500403 true 150 100 220 30

butterfly
true
0
Polygon -7500403 true true 150 165 209 199 225 225 225 255 195 270 165 255 150 240
Polygon -7500403 true true 150 165 89 198 75 225 75 255 105 270 135 255 150 240
Polygon -7500403 true true 139 148 100 105 55 90 25 90 10 105 10 135 25 180 40 195 85 194 139 163
Polygon -7500403 true true 162 150 200 105 245 90 275 90 290 105 290 135 275 180 260 195 215 195 162 165
Polygon -16777216 true false 150 255 135 225 120 150 135 120 150 105 165 120 180 150 165 225
Circle -16777216 true false 135 90 30
Line -16777216 false 150 105 195 60
Line -16777216 false 150 105 105 60

car
false
0
Polygon -7500403 true true 300 180 279 164 261 144 240 135 226 132 213 106 203 84 185 63 159 50 135 50 75 60 0 150 0 165 0 225 300 225 300 180
Circle -16777216 true false 180 180 90
Circle -16777216 true false 30 180 90
Polygon -16777216 true false 162 80 132 78 134 135 209 135 194 105 189 96 180 89
Circle -7500403 true true 47 195 58
Circle -7500403 true true 195 195 58

circle
false
0
Circle -7500403 true true 0 0 300

circle 2
false
0
Circle -7500403 true true 0 0 300
Circle -16777216 true false 30 30 240

cow
false
0
Polygon -7500403 true true 200 193 197 249 179 249 177 196 166 187 140 189 93 191 78 179 72 211 49 209 48 181 37 149 25 120 25 89 45 72 103 84 179 75 198 76 252 64 272 81 293 103 285 121 255 121 242 118 224 167
Polygon -7500403 true true 73 210 86 251 62 249 48 208
Polygon -7500403 true true 25 114 16 195 9 204 23 213 25 200 39 123

cylinder
false
0
Circle -7500403 true true 0 0 300

dot
false
0
Circle -7500403 true true 90 90 120

face happy
false
0
Circle -7500403 true true 8 8 285
Circle -16777216 true false 60 75 60
Circle -16777216 true false 180 75 60
Polygon -16777216 true false 150 255 90 239 62 213 47 191 67 179 90 203 109 218 150 225 192 218 210 203 227 181 251 194 236 217 212 240

face neutral
false
0
Circle -7500403 true true 8 7 285
Circle -16777216 true false 60 75 60
Circle -16777216 true false 180 75 60
Rectangle -16777216 true false 60 195 240 225

face sad
false
0
Circle -7500403 true true 8 8 285
Circle -16777216 true false 60 75 60
Circle -16777216 true false 180 75 60
Polygon -16777216 true false 150 168 90 184 62 210 47 232 67 244 90 220 109 205 150 198 192 205 210 220 227 242 251 229 236 206 212 183

fish
false
0
Polygon -1 true false 44 131 21 87 15 86 0 120 15 150 0 180 13 214 20 212 45 166
Polygon -1 true false 135 195 119 235 95 218 76 210 46 204 60 165
Polygon -1 true false 75 45 83 77 71 103 86 114 166 78 135 60
Polygon -7500403 true true 30 136 151 77 226 81 280 119 292 146 292 160 287 170 270 195 195 210 151 212 30 166
Circle -16777216 true false 215 106 30

flag
false
0
Rectangle -7500403 true true 60 15 75 300
Polygon -7500403 true true 90 150 270 90 90 30
Line -7500403 true 75 135 90 135
Line -7500403 true 75 45 90 45

flower
false
0
Polygon -10899396 true false 135 120 165 165 180 210 180 240 150 300 165 300 195 240 195 195 165 135
Circle -7500403 true true 85 132 38
Circle -7500403 true true 130 147 38
Circle -7500403 true true 192 85 38
Circle -7500403 true true 85 40 38
Circle -7500403 true true 177 40 38
Circle -7500403 true true 177 132 38
Circle -7500403 true true 70 85 38
Circle -7500403 true true 130 25 38
Circle -7500403 true true 96 51 108
Circle -16777216 true false 113 68 74
Polygon -10899396 true false 189 233 219 188 249 173 279 188 234 218
Polygon -10899396 true false 180 255 150 210 105 210 75 240 135 240

house
false
0
Rectangle -7500403 true true 45 120 255 285
Rectangle -16777216 true false 120 210 180 285
Polygon -7500403 true true 15 120 150 15 285 120
Line -16777216 false 30 120 270 120

leaf
false
0
Polygon -7500403 true true 150 210 135 195 120 210 60 210 30 195 60 180 60 165 15 135 30 120 15 105 40 104 45 90 60 90 90 105 105 120 120 120 105 60 120 60 135 30 150 15 165 30 180 60 195 60 180 120 195 120 210 105 240 90 255 90 263 104 285 105 270 120 285 135 240 165 240 180 270 195 240 210 180 210 165 195
Polygon -7500403 true true 135 195 135 240 120 255 105 255 105 285 135 285 165 240 165 195

line
true
0
Line -7500403 true 150 0 150 300

line half
true
0
Line -7500403 true 150 0 150 150

pentagon
false
0
Polygon -7500403 true true 150 15 15 120 60 285 240 285 285 120

person
false
0
Circle -7500403 true true 110 5 80
Polygon -7500403 true true 105 90 120 195 90 285 105 300 135 300 150 225 165 300 195 300 210 285 180 195 195 90
Rectangle -7500403 true true 127 79 172 94
Polygon -7500403 true true 195 90 240 150 225 180 165 105
Polygon -7500403 true true 105 90 60 150 75 180 135 105

plant
false
0
Rectangle -7500403 true true 135 90 165 300
Polygon -7500403 true true 135 255 90 210 45 195 75 255 135 285
Polygon -7500403 true true 165 255 210 210 255 195 225 255 165 285
Polygon -7500403 true true 135 180 90 135 45 120 75 180 135 210
Polygon -7500403 true true 165 180 165 210 225 180 255 120 210 135
Polygon -7500403 true true 135 105 90 60 45 45 75 105 135 135
Polygon -7500403 true true 165 105 165 135 225 105 255 45 210 60
Polygon -7500403 true true 135 90 120 45 150 15 180 45 165 90

sheep
false
15
Circle -1 true true 203 65 88
Circle -1 true true 70 65 162
Circle -1 true true 150 105 120
Polygon -7500403 true false 218 120 240 165 255 165 278 120
Circle -7500403 true false 214 72 67
Rectangle -1 true true 164 223 179 298
Polygon -1 true true 45 285 30 285 30 240 15 195 45 210
Circle -1 true true 3 83 150
Rectangle -1 true true 65 221 80 296
Polygon -1 true true 195 285 210 285 210 240 240 210 195 210
Polygon -7500403 true false 276 85 285 105 302 99 294 83
Polygon -7500403 true false 219 85 210 105 193 99 201 83

square
false
0
Rectangle -7500403 true true 30 30 270 270

square 2
false
0
Rectangle -7500403 true true 30 30 270 270
Rectangle -16777216 true false 60 60 240 240

star
false
0
Polygon -7500403 true true 151 1 185 108 298 108 207 175 242 282 151 216 59 282 94 175 3 108 116 108

target
false
0
Circle -7500403 true true 0 0 300
Circle -16777216 true false 30 30 240
Circle -7500403 true true 60 60 180
Circle -16777216 true false 90 90 120
Circle -7500403 true true 120 120 60

tree
false
0
Circle -7500403 true true 118 3 94
Rectangle -6459832 true false 120 195 180 300
Circle -7500403 true true 65 21 108
Circle -7500403 true true 116 41 127
Circle -7500403 true true 45 90 120
Circle -7500403 true true 104 74 152

triangle
false
0
Polygon -7500403 true true 150 30 15 255 285 255

triangle 2
false
0
Polygon -7500403 true true 150 30 15 255 285 255
Polygon -16777216 true false 151 99 225 223 75 224

truck
false
0
Rectangle -7500403 true true 4 45 195 187
Polygon -7500403 true true 296 193 296 150 259 134 244 104 208 104 207 194
Rectangle -1 true false 195 60 195 105
Polygon -16777216 true false 238 112 252 141 219 141 218 112
Circle -16777216 true false 234 174 42
Rectangle -7500403 true true 181 185 214 194
Circle -16777216 true false 144 174 42
Circle -16777216 true false 24 174 42
Circle -7500403 false true 24 174 42
Circle -7500403 false true 144 174 42
Circle -7500403 false true 234 174 42

turtle
true
0
Polygon -10899396 true false 215 204 240 233 246 254 228 266 215 252 193 210
Polygon -10899396 true false 195 90 225 75 245 75 260 89 269 108 261 124 240 105 225 105 210 105
Polygon -10899396 true false 105 90 75 75 55 75 40 89 31 108 39 124 60 105 75 105 90 105
Polygon -10899396 true false 132 85 134 64 107 51 108 17 150 2 192 18 192 52 169 65 172 87
Polygon -10899396 true false 85 204 60 233 54 254 72 266 85 252 107 210
Polygon -7500403 true true 119 75 179 75 209 101 224 135 220 225 175 261 128 261 81 224 74 135 88 99

wheel
false
0
Circle -7500403 true true 3 3 294
Circle -16777216 true false 30 30 240
Line -7500403 true 150 285 150 15
Line -7500403 true 15 150 285 150
Circle -7500403 true true 120 120 60
Line -7500403 true 216 40 79 269
Line -7500403 true 40 84 269 221
Line -7500403 true 40 216 269 79
Line -7500403 true 84 40 221 269

wolf
false
0
Polygon -16777216 true false 253 133 245 131 245 133
Polygon -7500403 true true 2 194 13 197 30 191 38 193 38 205 20 226 20 257 27 265 38 266 40 260 31 253 31 230 60 206 68 198 75 209 66 228 65 243 82 261 84 268 100 267 103 261 77 239 79 231 100 207 98 196 119 201 143 202 160 195 166 210 172 213 173 238 167 251 160 248 154 265 169 264 178 247 186 240 198 260 200 271 217 271 219 262 207 258 195 230 192 198 210 184 227 164 242 144 259 145 284 151 277 141 293 140 299 134 297 127 273 119 270 105
Polygon -7500403 true true -1 195 14 180 36 166 40 153 53 140 82 131 134 133 159 126 188 115 227 108 236 102 238 98 268 86 269 92 281 87 269 103 269 113

x
false
0
Polygon -7500403 true true 270 75 225 30 30 225 75 270
Polygon -7500403 true true 30 75 75 30 270 225 225 270
@#$#@#$#@
NetLogo 6.2.0
@#$#@#$#@
@#$#@#$#@
@#$#@#$#@
<experiments>
  <experiment name="experimento1-cantidad-aves" repetitions="4" runMetricsEveryStep="false">
    <setup>setup</setup>
    <go>go</go>
    <metric>maximo-energia</metric>
    <metric>tick-maximo-energia</metric>
    <metric>aves-sobrevivientes</metric>
    <metric>porcentaje-aves-sobrevivientes</metric>
    <enumeratedValueSet variable="numero-nidos">
      <value value="50"/>
    </enumeratedValueSet>
    <steppedValueSet variable="cantidad-ave1" first="1" step="1" last="50"/>
    <steppedValueSet variable="cantidad-ave2" first="1" step="1" last="50"/>
    <enumeratedValueSet variable="porcentaje-abundancia">
      <value value="80"/>
    </enumeratedValueSet>
  </experiment>
  <experiment name="experimento2-porcentaje-comida" repetitions="30" runMetricsEveryStep="false">
    <setup>setup</setup>
    <go>go</go>
    <metric>maximo-energia</metric>
    <metric>tick-maximo-energia</metric>
    <metric>aves-sobrevivientes</metric>
    <metric>porcentaje-aves-sobrevivientes</metric>
    <enumeratedValueSet variable="numero-nidos">
      <value value="50"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="cantidad-ave1">
      <value value="22"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="cantidad-ave2">
      <value value="18"/>
    </enumeratedValueSet>
    <steppedValueSet variable="porcentaje-abundancia" first="0" step="1" last="100"/>
  </experiment>
  <experiment name="experimento3-cantidad-nidos" repetitions="30" runMetricsEveryStep="false">
    <setup>setup</setup>
    <go>go</go>
    <metric>maximo-energia</metric>
    <metric>tick-maximo-energia</metric>
    <metric>aves-sobrevivientes</metric>
    <metric>porcentaje-aves-sobrevivientes</metric>
    <steppedValueSet variable="numero-nidos" first="1" step="1" last="100"/>
    <enumeratedValueSet variable="cantidad-ave1">
      <value value="22"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="cantidad-ave2">
      <value value="18"/>
    </enumeratedValueSet>
    <enumeratedValueSet variable="porcentaje-abundancia">
      <value value="80"/>
    </enumeratedValueSet>
  </experiment>
</experiments>
@#$#@#$#@
@#$#@#$#@
default
0.0
-0.2 0 0.0 1.0
0.0 1 1.0 0.0
0.2 0 0.0 1.0
link direction
true
0
Line -7500403 true 150 150 90 180
Line -7500403 true 150 150 210 180
@#$#@#$#@
0
@#$#@#$#@
