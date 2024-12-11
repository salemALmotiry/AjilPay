# **README: Update summary**

---

## **Features Overview**

This update includes three core functionalities to manage store-customer interactions and optimize the invoice workflow. It also introduces global exception handling to provide clear error messages across the application.

---

### **1. Customer Management Flows**

#### **1.1 Customer Checkout**
- A customer can "check out" from a store when all their payments are cleared.  
- On checkout:
  - Any unpaid invoices must be settled.
  - The `storeId` in the customer record is set to `null`.

#### **1.2 Customer Assignment by Store Owner**
- A store owner can assign a customer to their store.  
- Customers with `storeId = null` can be linked to the store by their ID.
- **Validation:**
  - If the customer is already associated with a store, an exception is thrown.
  - If the store or customer doesn’t exist, an exception is thrown.
    
#### **1.3 Customer checkout by Store Owner**
- A store owner can perform a customer checkout to remove the customer from their store.  
- On checkout:
  - Any unpaid invoices must be settled.
  - The `storeId` in the customer record is set to `null`.
- **Validation:**
  - If the customer has unpaid invoices, an exception is thrown.
  - If the store or customer doesn’t exist, an exception is thrown.

---

### **2. Invoice Optimization Workflow**

#### **2.1 Store Adds Invoice and Items in One Request**
- The store can create an invoice and its associated items in a single API call.  
- **Validation Rules**
- The total price of all items must match the invoice's totalAmount.
- **Example JSON Request:**
```json
{
  "invoice": {
    "totalAmount": 200.00
  },
  "items": [
    {
      "itemName": "Rice",
      "quantity": 2,
      "pricePerUnit": 50.00
    },
    {
      "itemName": "Beans",
      "quantity": 3,
      "pricePerUnit": 20.00
    }
  ]
}
```
---

#### **2.2 Add Invoice Items Separately**
- If the store missed adding items during invoice creation, they can add items later via a separate endpoint.
- **Validation:**
  - The cumulative price of added items should not exceed the `invoice.totalAmount`.

#### **2.3 Customer Adds Items to Invoice**
- Customers can add items to an invoice if the store hasn’t already added them.
- **Validation:**
  - The total price of items added by the customer should not exceed the `invoice.totalAmount`.

---

### **3. Global Exception Handling**

#### **ControllerAdvices Implementation**
- A centralized exception handler (`@ControllerAdvice`) is used to manage errors consistently.
- Exceptions are caught and return user-friendly error messages.

---

### **Sample Exception Messages**
| **Scenario**                        | **Error Message**                                           |
|-------------------------------------|------------------------------------------------------------|
| Store not found                     | `"Store not found"`                                         |
| Customer not found                  | `"Customer not found"`                                      |
| Customer already associated         | `"Customer is already associated with a store"`            |
| Invoice not found                   | `"Invoice not found"`                                       |
| Total item price mismatch for invoice | `"Total item prices do not match invoice total amount"`      |
| Total added items exceed invoice total | `"Added items exceed the invoice's total amount"`          |

---

### **Endpoints Overview**

| **Endpoint**                                      | **Method** | **Description**                                   |
|--------------------------------------------------|-----------|-------------------------------------------------|
| `/customer/{customerId}/checkout`               | `PUT`     | Customer checks out from the store              |
| `/store/{customerId}/checkout`               | `PUT`     | Customer checkout by Store Owner              |
| `/store/{storeId}/add-customer/{customerId}`    | `PUT`     | Store assigns a customer to their store         |
| `/store/{storeId}/add-invoice`                  | `POST`    | Store adds an invoice and associated items      |
| `/store/{storeId}/add-invoice-items/{invoiceId}` | `POST`    | Store adds items to an existing invoice         |
| `/customer/{customerId}/add-invoice-items/{invoiceId}` | `POST` | Customer adds items to their invoice           |

--- 

### **Total Endpoints: 18**

---

#### **Store Owner (11 Endpoints)**

1. **`createMonthlyPayment`**  
   Generate monthly payment details for customers.

2. **`processCustomerPayment`**  
   Apply payments made by customers and update balances.

3. **`analyzePaymentBehavior`**  
   Analyze customer payment trends and behaviors.

4. **`getCustomerOverView`**  
   Get a detailed overview of a specific customer's activity in the store.

5. **`getStoreInsights`**  
   Provide overall store performance insights, including revenue and trends.

6. **`getTopSellingItems`**  
   Identify top-selling items based on historical sales data.

7. **`evaluateCustomerTrustworthiness`**  
   Assess a customer's trustworthiness based on payment history and behavior.

8. **`forecastStoreSales`**  
   Predict future sales for the store based on past data and trends.

9. **`detectSuspiciousActivity`**  
   Identify suspicious activity such as unusually high returns or refunds.

10. **`detectAbnormalSpending`**  
    Spot irregular spending patterns in customer purchases.

11. **`assignCustomerToStore`**  
    Link a customer to the store if not already associated.
12.  **`checkoutFromStore`**  
  A store owner can perform a customer checkout to remove the customer from their store.

---

#### **Customer (7 Endpoints)**

1. **`getInvoiceSummaryForCustomer`**  
   View a summary of all invoices associated with the customer.

2. **`addItemToInvoice`**  
   Allow the customer to add items to their invoice (within store-defined limits).

3. **`getUnusualItems`**  
   Identify unusual items in the customer's purchase history.

4. **`forecastCustomerInvoices`**  
   Predict future invoice amounts based on customer purchase trends.

5. **`getPriceChangesForCustomer`**  
   Track price fluctuations for items frequently purchased by the customer.

6. **`checkoutFromStore`**  
   Enable the customer to finalize payments and exit the store association.

7. **`analyzeCustomerPurchasePatterns`**  
   Provide insights into the customer’s purchase habits, such as recurring purchases, seasonal items, and preferred categories.

