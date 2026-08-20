package com.Santino.Pagos.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Payload que mandaría el PSP. El cobro interno usa el mismo servicio con datos del evento Rabbit.")
public record PagoRequest(
        @Schema(description = "Usuario dueño de la suscripción", example = "7") Long userID,
        @Schema(description = "Email para la notificación", example = "ana@mail.com") String email,
        @Schema(description = "Id de la suscripción cobrada", example = "50") Long suscripcionID,
        @Schema(description = "Monto", example = "1500") Long monto,
        @Schema(description = "Nombre del plan", example = "premium") String plan) {

}
