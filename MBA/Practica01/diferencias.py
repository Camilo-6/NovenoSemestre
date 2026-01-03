import csv

# Funcion para leer los datos de un archivo CSV
def leer_csv(ruta):
    datos = {}
    with open(ruta, newline="") as f:
        reader = csv.reader(f)
        for fila in reader:
            if len(fila) == 3:
                # Ir guardando los datos en un diccionario
                x, y, color = int(fila[0]), int(fila[1]), float(fila[2])
                datos[(x, y)] = color
    return datos

# Archivos
archivo1 = "sin-perturbar.csv"
archivo2 = "perturbado.csv"
archivo3 = "diferencias.csv"

# Leer los datos de ambos archivos
datos1 = leer_csv(archivo1)
datos2 = leer_csv(archivo2)

# Nuevo color
verde = 55

# Funcion para buscar la diferencia entre los datos y escribir el resultado en un nuevo archivo CSV
with open(archivo3, "w", newline="") as f:
    writer = csv.writer(f)
    # Iterar sobre las coordenadas
    for coord in sorted(datos1.keys()):
        color1 = datos1.get(coord, 0)
        color2 = datos2.get(coord, 0)
        if color1 == color2:
            # Si son iguales, escribir el color original
            writer.writerow([coord[0], coord[1], color1])
        else:
            # Si son diferentes, escribir el nuevo color verde
            writer.writerow([coord[0], coord[1], verde])