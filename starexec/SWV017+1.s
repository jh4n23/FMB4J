% SZS status Satisfiable for SWV017+1
% SZS output start FiniteModel for SWV017+1
fof(domain, interpretation-domains,
        ![X]: (X = "d0" | X = "d1")
).

fof(mappings, interpretation-mappings,
          sent("d0", "d0", "d0") = "d1"
        & sent("d1", "d1", "d0") = "d1"
        & sent("d0", "d0", "d1") = "d0"
        & sent("d1", "d1", "d1") = "d0"
        & sent("d0", "d1", "d1") = "d0"
        & sent("d0", "d1", "d0") = "d1"
        & sent("d1", "d0", "d0") = "d1"
        & sent("d1", "d0", "d1") = "d0"
        & pair("d0", "d0") = "d0"
        & pair("d0", "d1") = "d0"
        & pair("d1", "d0") = "d0"
        & pair("d1", "d1") = "d1"
        & quadruple("d0", "d0", "d1", "d0") = "d0"
        & quadruple("d0", "d0", "d0", "d1") = "d0"
        & quadruple("d1", "d0", "d0", "d0") = "d0"
        & quadruple("d1", "d1", "d1", "d1") = "d1"
        & quadruple("d1", "d1", "d0", "d1") = "d0"
        & quadruple("d0", "d1", "d1", "d1") = "d0"
        & quadruple("d1", "d1", "d1", "d0") = "d0"
        & quadruple("d1", "d0", "d1", "d1") = "d0"
        & quadruple("d0", "d1", "d0", "d0") = "d0"
        & quadruple("d0", "d0", "d0", "d0") = "d0"
        & quadruple("d0", "d1", "d0", "d1") = "d0"
        & quadruple("d0", "d1", "d1", "d0") = "d0"
        & quadruple("d0", "d0", "d1", "d1") = "d0"
        & quadruple("d1", "d0", "d0", "d1") = "d0"
        & quadruple("d1", "d0", "d1", "d0") = "d0"
        & quadruple("d1", "d1", "d0", "d0") = "d0"
        & generate_key("d0") = "d0"
        & generate_key("d1") = "d0"
        & t = "d0"
        & triple("d0", "d0", "d0") = "d0"
        & triple("d1", "d0", "d1") = "d0"
        & triple("d1", "d1", "d0") = "d0"
        & triple("d0", "d1", "d1") = "d0"
        & triple("d1", "d1", "d1") = "d1"
        & triple("d1", "d0", "d0") = "d0"
        & triple("d0", "d0", "d1") = "d0"
        & triple("d0", "d1", "d0") = "d0"
        & an_intruder_nonce = "d1"
        & a = "d1"
        & b = "d1"
        & encrypt("d1", "d0") = "d1"
        & encrypt("d0", "d1") = "d1"
        & encrypt("d0", "d0") = "d1"
        & encrypt("d1", "d1") = "d1"
        & generate_intruder_nonce("d0") = "d0"
        & generate_intruder_nonce("d1") = "d1"
        & bt = "d1"
        & key("d0", "d1") = "d1"
        & key("d1", "d1") = "d0"
        & key("d0", "d0") = "d1"
        & key("d1", "d0") = "d0"
        & generate_expiration_time("d1") = "d1"
        & generate_expiration_time("d0") = "d1"
        & at = "d1"
        & generate_b_nonce("d1") = "d1"
        & generate_b_nonce("d0") = "d1"
        & an_a_nonce = "d1"
).

fof(predicate_mappings, interpretation-mappings,
           party_of_protocol("d1")
        &  party_of_protocol("d0")
        & ~fresh_intruder_nonce("d0")
        &  fresh_intruder_nonce("d1")
        &  a_stored("d0")
        &  a_stored("d1")
        & ~fresh_to_b("d0")
        &  fresh_to_b("d1")
        & ~message("d1")
        &  message("d0")
        & ~intruder_holds("d1")
        &  intruder_holds("d0")
        & ~a_key("d1")
        &  a_key("d0")
        &  a_nonce("d1")
        & ~a_nonce("d0")
        & ~intruder_message("d0")
        &  intruder_message("d1")
        &  t_holds("d0")
        & ~t_holds("d1")
).
% SZS output end FiniteModel for SWV017+1

