ALTER TABLE "VISPOD"."TIIVS_SOLICITUD" ADD (FLAG_REIMPRIMIR VARCHAR2(1) DEFAULT NULL );

update "VISPOD"."tiivs_solicitud" set flag_reimprimir =0 ;