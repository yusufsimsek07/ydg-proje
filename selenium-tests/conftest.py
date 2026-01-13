import pytest
from selenium import webdriver
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.remote.webdriver import WebDriver


@pytest.fixture(scope="session")
def driver() -> WebDriver:
    options = Options()
    options.add_argument("--headless")
    options.add_argument("--no-sandbox")
    options.add_argument("--disable-dev-shm-usage")
    options.add_argument("--disable-gpu")
    options.add_argument("--window-size=1920,1080")
    
    
    # Retry logic for connecting to Chrome (container might take a few seconds to start)
    import time
    
    driver = None
    max_retries = 30
    for i in range(max_retries):
        try:
            print(f"Attempting to connect to Chrome driver (attempt {i+1}/{max_retries})...")
            driver = webdriver.Remote(
                command_executor="http://chrome:4444/wd/hub",
                options=options
            )
            print("Successfully connected to Chrome driver!")
            break
        except Exception as e:
            print(f"Connection failed: {e}")
            if i < max_retries - 1:
                time.sleep(1)
            else:
                raise Exception("Could not connect to Chrome driver after multiple attempts") from e
    
    yield driver
    if driver:
        driver.quit()


@pytest.fixture
def base_url():
    return "http://backend:8080"
