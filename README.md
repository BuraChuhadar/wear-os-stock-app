# Wear OS Stock Market App

A simple stock market app for Wear OS devices that displays stock prices and price changes.

## Features

- Display stock prices and price changes from CNBC and Finnhub.io
- Add stock symbols to track
- Refresh stock prices with a button click
- Customize stock list items with colors based on positive/negative changes
- Dark mode support

## Prerequisites

Before you begin, you will need to obtain an API key from [Finnhub.io](https://finnhub.io/). Create a free account, and copy your API key.

Create a file named `secrets.xml` in the `app/src/main/res/values` directory, and add your API key as follows:

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="Finnhub_API_KEY">your_api_key_here</string>
</resources>
```

Replace your_api_key_here with the API key you obtained from Finnhub.io.

## Installation

1. Clone this repository:

```sh
git clone https://github.com/your_username/wear-os-stock-app.git
```

2. Open the project in Android Studio.
3. Set up an Android Wear emulator or connect a Wear OS device.
4. Press the "Run" button in Android Studio to build and install the app.

## Usage

1. Use the "+" button to add stock symbols. The app will fetch stock prices and display them in a list.
2. Press the "refresh" button to update the stock prices.
3. Stock prices and their changes will be displayed with green (positive change) or red (negative change) colors.

## Contributing

1. Fork the repository on GitHub.
2. Clone your fork of the repository.
3. Create a new branch for your changes.
4. Commit your changes and push them to your branch.
5. Create a pull request on GitHub for your changes.

## License

This project is licensed under the [MIT License](https://opensource.org/license/mit/).
