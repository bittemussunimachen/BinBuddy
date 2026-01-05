import os
import sys
import urllib.parse
import urllib.request


def fetch(ean: str, timeout: int = 8) -> str:
    url = f"https://world.openfoodfacts.org/api/v0/product/{urllib.parse.quote(ean)}.json"
    with urllib.request.urlopen(url, timeout=timeout) as resp:
        return resp.read().decode("utf-8", errors="replace")


def parse(response: str):
    import json
    data = json.loads(response)
    status = data.get("status", 0)
    if status != 1:
        return status, None
    product = data.get("product", {}) or {}
    return 0, product


def main():
    if len(sys.argv) != 2:
        print("Usage: python scripts/opengtin_test.py <EAN>")
        sys.exit(1)

    ean = sys.argv[1]
    try:
        raw = fetch(ean)
    except Exception as exc:  # pragma: no cover - simple CLI helper
        print(f"Request failed: {exc}")
        sys.exit(1)

    error_code, product = parse(raw)
    if error_code:
        print(f"API error code: {error_code}")
        sys.exit(1)

    name = product.get("product_name", "Unknown")
    brand = product.get("brands", "")
    quantity = product.get("quantity", "")
    categories = product.get("categories", "")
    packaging = product.get("packaging", product.get("packaging_tags", ""))
    labels = product.get("labels", "")
    generic = product.get("generic_name", "")

    print(f"Name       : {name}")
    if brand:
        print(f"Brand      : {brand}")
    if generic:
        print(f"Generic    : {generic}")
    if quantity:
        print(f"Quantity   : {quantity}")
    if categories:
        print(f"Categories : {categories}")
    if packaging:
        print(f"Packaging  : {packaging}")
    if labels:
        print(f"Labels     : {labels}")


if __name__ == "__main__":
    main()

