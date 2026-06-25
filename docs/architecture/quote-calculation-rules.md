# Quote Calculation Rules

## Numeric Representation

- Money is stored as `Long` minor units, for example cents.
- Quantity is stored as `Long` thousandths.
- Percent values are stored as basis points where `10000` means `100.00%`.
- Calculations use integer arithmetic with centralized half-up division for percentage and quantity scaling.
- `Float` and `Double` are not used for money, percentages, discounts, tax, subtotal, or total.

## Discount Types

```text
NONE
FIXED
PERCENT
```

- `NONE`: discount value is ignored.
- `FIXED`: value is money in minor units.
- `PERCENT`: value is basis points.

## Calculation Order

1. Gross line total = `quantityThousandths * unitPriceMinor / 1000`, rounded half up.
2. Line discount = fixed or percentage discount applied to gross line total.
3. Net line total = gross line total minus line discount.
4. Subtotal = sum of net line totals.
5. Global discount = fixed or percentage discount applied to subtotal.
6. Tax base = subtotal minus global discount.
7. Tax amount = tax base multiplied by tax rate basis points when tax is enabled.
8. Total = tax base plus tax amount.

## Safety Rules

- No result may be negative.
- Fixed discounts cannot exceed their base.
- Percentage discounts must be between `0` and `10000`.
- Tax rate must be between `0` and `10000`.
- Tax label is required when tax is enabled.
- Quote totals persisted in Room come from the domain calculator, not from UI-submitted totals.
- A quote must contain at least one valid line item before it is saved.

## Rounding

Rounding uses half-up integer division:

```text
(numerator + denominator / 2) / denominator
```

The same calculator is used by ViewModels, use cases, and persisted quote creation/update paths.
