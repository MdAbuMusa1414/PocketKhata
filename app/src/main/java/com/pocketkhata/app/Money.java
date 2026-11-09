package com.pocketkhata.app;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;
public final class Money {
 private Money(){}
 public static long parse(String value){
  if(value==null||!value.trim().matches("[0-9]{1,7}(\\.[0-9]{1,2})?"))throw new IllegalArgumentException("Enter a valid amount, up to 2 decimal places");
  long paisa=new BigDecimal(value.trim()).movePointRight(2).setScale(0,RoundingMode.UNNECESSARY).longValueExact();
  if(paisa<=0)throw new IllegalArgumentException("Amount must be greater than zero");return paisa;
 }
 public static String format(long paisa){NumberFormat n=NumberFormat.getNumberInstance(Locale.US);n.setMinimumFractionDigits(2);n.setMaximumFractionDigits(2);return "৳ "+n.format(BigDecimal.valueOf(paisa,2));}
 public static String editable(long paisa){return BigDecimal.valueOf(paisa,2).toPlainString();}
}
