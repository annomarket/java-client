/*
 * Copyright (c) 2014 The University of Sheffield
 *
 * This file is part of the AnnoMarket.com REST client library, and is
 * licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.annomarket.cli.commands;

import com.annomarket.client.RestClient;
import com.annomarket.shop.Item;
import com.annomarket.shop.Shop;

public class ItemDetails extends AbstractCommand {

  public void run(RestClient client, String... args) throws Exception {
    if(args.length < 1) {
      System.err.println("Usage: item-details <itemid>");
      System.exit(1);
    }
    long itemId = -1;
    try {
      itemId = Long.parseLong(args[0]);
    } catch(NumberFormatException e) {
      System.err.println("Item ID must be a valid number");
      System.exit(1);
    }
    Shop shop = new Shop(client);
    Item i = shop.getItem(itemId);
    renderItem(i);
  }

  private void renderItem(Item i) {
    System.out.println("   ID: " + i.id);
    System.out.println(" Name: " + i.name);
    System.out.println("Price: " + formatPrices(i.price));
    System.out.println();
    System.out.println("Description");
    System.out.println("-----------");
    System.out.println();
    System.out.println(i.shortDescription);
  }
  
  

}
