/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package com.andrei.food.ordering.system.kafka.order.avro.model;
@org.apache.avro.specific.AvroGenerated
public enum PaymentStatus implements org.apache.avro.generic.GenericEnumSymbol<PaymentStatus> {
  COMPLETED, CANCELLED, FAILED  ;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"enum\",\"name\":\"PaymentStatus\",\"namespace\":\"com.andrei.food.ordering.system.kafka.order.avro.model\",\"symbols\":[\"COMPLETED\",\"CANCELLED\",\"FAILED\"]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  @Override
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
}
