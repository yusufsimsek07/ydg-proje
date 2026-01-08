import pytest
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC


def test_01_login_and_dashboard(driver, base_url):
    """Test auditor login and dashboard visibility"""
    driver.get(f"{base_url}/login")
    
    # Wait for login form
    username_field = WebDriverWait(driver, 10).until(
        EC.presence_of_element_located((By.ID, "username"))
    )
    password_field = driver.find_element(By.ID, "password")
    submit_button = driver.find_element(By.ID, "submit")
    
    # Login as auditor
    username_field.send_keys("auditor")
    password_field.send_keys("Auditor123!")
    submit_button.click()
    
    # Wait for dashboard
    WebDriverWait(driver, 10).until(
        EC.presence_of_element_located((By.XPATH, "//h1[contains(text(), 'Welcome')]"))
    )
    
    # Verify dashboard is visible
    assert "Dashboard" in driver.title or "Welcome" in driver.page_source
    assert driver.current_url.endswith("/dashboard") or "/dashboard" in driver.current_url
