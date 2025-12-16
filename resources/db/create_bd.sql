# CONSULTAS ALQUILER

select-findAllAlquileres = SELECT id, idInscripcion, matricula, fechaInicio, fechaFin, plazasReservadas, precioTotal FROM Alquiler;
select-findAlquilerById = SELECT id, idInscripcion, matricula, fechaInicio, fechaFin, plazasReservadas, precioTotal FROM Alquiler WHERE id=?;
select-findAlquileresByMatricula = SELECT id, idInscripcion, matricula, fechaInicio, fechaFin, plazasReservadas, precioTotal FROM Alquiler WHERE matricula=?;
insert-addAlquiler = INSERT INTO Alquiler (idInscripcion, matricula, fechaInicio, fechaFin, plazasReservadas, precioTotal) VALUES (?,?,?,?,?,?);
delete-alquilerById = DELETE FROM Alquiler WHERE id=?;


# CONSULTAS ALQUILERSOCIO

select-findAllAlquilerSocios = SELECT idAlquiler, dniSocio FROM AlquilerSocio;
select-findAlquilerSocioByIdAlquiler = SELECT idAlquiler, dniSocio FROM AlquilerSocio WHERE idAlquiler=?;
select-findAlquilerSocioByDni = SELECT idAlquiler, dniSocio FROM AlquilerSocio WHERE dniSocio=?;
insert-addAlquilerSocio = INSERT INTO AlquilerSocio (idAlquiler, dniSocio) VALUES (?, ?);
delete-deleteAlquilerSocio = DELETE FROM AlquilerSocio WHERE idAlquiler=? AND dniSocio=?;


# CONSULTAS EMBARCACION

select-findAllEmbarcaciones = SELECT matricula, nombre, tipo, numPlazas, dimensiones, dniPatron FROM Embarcacion;
select-findEmbarcacionByMatricula = SELECT matricula, nombre, tipo, numPlazas, dimensiones, dniPatron FROM Embarcacion WHERE matricula=?;
insert-addEmbarcacion = INSERT INTO Embarcacion (matricula, nombre, tipo, numPlazas, dimensiones, dniPatron) VALUES (?, ?, ?, ?, ?, ?);
update-updateEmbarcacion = UPDATE Embarcacion SET nombre=?, tipo=?, numPlazas=?, dimensiones=?, dniPatron=? WHERE matricula=?;
delete-deleteEmbarcacionByMatricula = DELETE FROM Embarcacion WHERE matricula=?;
select-findEmbarcacionByTipo = SELECT matricula, nombre, tipo, numPlazas, dimensiones, dniPatron FROM Embarcacion WHERE UPPER(tipo)=?;
select-findEmbarcacionByNombre = SELECT matricula, nombre, tipo, numPlazas, dimensiones, dniPatron FROM Embarcacion WHERE nombre=?
select-findEmbarcacionesDisponibles = \
    SELECT * FROM Embarcacion \
    WHERE matricula NOT IN ( \
        SELECT matricula FROM Alquiler \
        WHERE (fechaInicio <= ? AND fechaFin >= ?) \
        OR (fechaInicio BETWEEN ? AND ?) \
        OR (fechaFin BETWEEN ? AND ?) \
    ) \
    AND matricula NOT IN ( \
        SELECT matricula FROM Reserva \
        WHERE fecha BETWEEN ? AND ? \
    )


# CONSULTAS INSCRIPCION

select-findAllInscripciones = SELECT id, fechaCreacion, tipo, cuotaActual, dniTitular FROM Inscripcion;
select-findInscripcionById = SELECT id, fechaCreacion, tipo, cuotaActual, dniTitular FROM Inscripcion WHERE id=?;
select-findInscripcionesByTipo = SELECT id, fechaCreacion, tipo, cuotaActual, dniTitular FROM Inscripcion WHERE tipo=?;
insert-addInscripcion = INSERT INTO Inscripcion (fechaCreacion, tipo, cuotaActual, dniTitular) VALUES (?, ?, ?, ?);
update-updateInscripcion = UPDATE Inscripcion SET fechaCreacion=?, tipo=?, cuotaActual=?, dniTitular=? WHERE id=?;
delete-deleteInscripcionById = DELETE FROM Inscripcion WHERE id=?;


# CONSULTAS PATRON

select-findAllPatrones = SELECT dni, nombre, apellidos, fechaNacimiento, fechaExpedicionTitulo FROM Patron;
select-findPatronByDni = SELECT dni, nombre, apellidos, fechaNacimiento, fechaExpedicionTitulo FROM Patron WHERE dni=?;
insert-addPatron = INSERT INTO Patron (dni, nombre, apellidos, fechaNacimiento, fechaExpedicionTitulo) VALUES (?, ?, ?, ?, ?);
update-updatePatron = UPDATE Patron SET nombre=?, apellidos=?, fechaNacimiento=?, fechaExpedicionTitulo=? WHERE dni=?;
delete-deletePatronByDni = DELETE FROM Patron WHERE dni=?;


# CONSULTAS RESERVA

select-findAllReservas = SELECT id, dniSocio, matricula, fecha, plazasReservadas, proposito, precioTotal FROM Reserva;
select-findReservaById = SELECT id, dniSocio, matricula, fecha, plazasReservadas, proposito, precioTotal FROM Reserva WHERE id=?;
insert-addReserva = INSERT INTO Reserva (dniSocio, matricula, fecha, plazasReservadas, proposito, precioTotal) VALUES (?, ?, ?, ?, ?, ?);
update-updateReserva = UPDATE Reserva SET dniSocio=?, matricula=?, fecha=?, plazasReservadas=?, proposito=?, precioTotal=? WHERE id=?;
delete-deleteReservaById = DELETE FROM Reserva WHERE id=?;
select-isReservaDisponible = SELECT COUNT(*) FROM Reserva WHERE matricula = ? AND fecha = ?
select-isAlquilerDisponible = SELECT COUNT(*) FROM Alquiler WHERE matricula = ? AND ? BETWEEN fechaInicio AND fechaFin


# CONSULTAS SOCIO

select-findAllSocios = SELECT dni, nombre, apellidos, fechaNacimiento, direccion, fechaInscripcion, esTitular, tieneTituloPatron FROM Socio;
select-findSocioByDni = SELECT dni, nombre, apellidos, fechaNacimiento, direccion, fechaInscripcion, esTitular, tieneTituloPatron FROM Socio WHERE dni=?;
insert-addSocio = INSERT INTO Socio (dni, nombre, apellidos, fechaNacimiento, direccion, fechaInscripcion, esTitular, tieneTituloPatron) VALUES (?, ?, ?, ?, ?, ?, ?, ?);
update-updateSocio = UPDATE Socio SET nombre=?, apellidos=?, fechaNacimiento=?, direccion=?, fechaInscripcion=?, esTitular=?, tieneTituloPatron=? WHERE dni=?;
delete-deleteSocioByDni = DELETE FROM Socio WHERE dni=?;
update-setTituloPatron = UPDATE Socio SET tieneTituloPatron=? WHERE dni=?


# CONSULTAS SOCIOINSCRIPCION

select-findAllSocioInscripciones = SELECT id, idInscripcion, dniSocio FROM SocioInscripcion;
select-findSocioInscripcionByIdInscripcion = SELECT id, idInscripcion, dniSocio FROM SocioInscripcion WHERE idInscripcion=?;
select-findSocioInscripcionByDni = SELECT id, idInscripcion, dniSocio FROM SocioInscripcion WHERE dniSocio=?;
insert-addSocioInscripcion = INSERT INTO SocioInscripcion (idInscripcion, dniSocio) VALUES (?, ?);
delete-deleteSocioInscripcion = DELETE FROM SocioInscripcion WHERE idInscripcion=? AND dniSocio=?;
select-countByInscripcionId = SELECT COUNT(*) FROM SocioInscripcion WHERE idInscripcion = ?