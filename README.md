
# Stock Search Application

A simple Android application that allows users to search for stocks using the [Finnhub API](https://finnhub.io/).

## Features

-   Search for stocks by symbol
-   View basic information about a stock, including its name and exchange
-   View real-time price data for a stock

## Requirements

-   Android Studio 4.1 or later
-   Android API 21 (Lollipop) or later
-   An API key from Finnhub (sign up for a free API key [here](https://finnhub.io/))

## Installation

1.  Clone or download this repository to your local machine.
2.  Open the project in Android Studio.
3.  In the `gradle.properties` file, replace `YOUR_API_KEY_HERE` with your Finnhub API key.
4.  Build and run the project on an emulator or physical device.

## Technical Details

This application was built using Android Studio and Java. It makes use of the following libraries and tools:

-   [Retrofit](https://square.github.io/retrofit/) for API calls
-   [Gson](https://github.com/google/gson) for JSON parsing
-   [OkHttp](https://square.github.io/okhttp/) for networking

## Contribute

This project is open source and contributions are welcome. If you find a bug or have a feature request, please open an issue. If you would like to contribute code, please fork the repository and submit a pull request.
