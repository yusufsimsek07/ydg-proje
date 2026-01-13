import pytest
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from datetime import datetime


def test_02_create_facility_and_audit(driver, base_url):
    """Test creating facility and audit, then verify audit detail opens"""
    driver.get(f"{base_url}/login")
    
    # Login as auditor
    WebDriverWait(driver, 30).until(
        EC.presence_of_element_located((By.ID, "username"))
    )
    driver.find_element(By.ID, "username").send_keys("auditor")
    driver.find_element(By.ID, "password").send_keys("Auditor123!")
    
    submit_btn = driver.find_element(By.ID, "submit")
    driver.execute_script("arguments[0].click();", submit_btn)
    
    # Wait for dashboard
    WebDriverWait(driver, 30).until(
        EC.presence_of_element_located((By.XPATH, "//a[contains(@href, '/auditor/facilities')]"))
    )
    
    # Navigate to facilities
    driver.get(f"{base_url}/auditor/facilities/new")
    
    # Fill facility form
    WebDriverWait(driver, 30).until(
        EC.presence_of_element_located((By.ID, "facility-name"))
    )
    driver.find_element(By.ID, "facility-name").send_keys(f"Test Facility {datetime.now().strftime('%Y%m%d%H%M%S')}")
    driver.find_element(By.ID, "facility-address").send_keys("123 Test Street")
    driver.find_element(By.ID, "facility-city").send_keys("Test City")
    driver.find_element(By.ID, "facility-contact-name").send_keys("Test Contact")
    driver.find_element(By.ID, "facility-contact-phone").send_keys("555-1234")
    
    submit_btn = driver.find_element(By.ID, "submit")
    driver.execute_script("arguments[0].click();", submit_btn)
    
    # Wait for facilities list
    WebDriverWait(driver, 30).until(
        EC.presence_of_element_located((By.XPATH, "//h1[contains(text(), 'Facilities')]"))
    )
    
    # Navigate to create audit
    driver.get(f"{base_url}/auditor/audits/new")
    
    # Wait for audit form
    WebDriverWait(driver, 30).until(
        EC.presence_of_element_located((By.ID, "audit-facility"))
    )
    
    # Select first facility
    from selenium.webdriver.support.ui import Select
    facility_select = Select(driver.find_element(By.ID, "audit-facility"))
    facility_select.select_by_index(1)  # First option after "Select Facility"
    
    # Set audit date
    date_input = driver.find_element(By.ID, "audit-date")
    current_date = datetime.now().strftime("%Y-%m-%d")
    driver.execute_script(f"arguments[0].value = '{current_date}';", date_input)
    
    # Submit
    submit_btn = driver.find_element(By.ID, "submit")
    driver.execute_script("arguments[0].click();", submit_btn)
    
    # Wait for audit detail page
    WebDriverWait(driver, 30).until(
        EC.presence_of_element_located((By.XPATH, "//h1[contains(text(), 'Audit #')]"))
    )
    
    # Verify audit detail is open
    assert "Audit" in driver.page_source
