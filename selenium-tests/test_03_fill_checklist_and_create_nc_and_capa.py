import pytest
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait, Select
from selenium.webdriver.support import expected_conditions as EC
from datetime import datetime, timedelta


def test_03_fill_checklist_and_create_nc_and_capa(driver, base_url):
    """Test: fill checklist with FAIL -> create NC -> manager login -> create CA -> mark DONE -> close NC -> verify CLOSED"""
    
    # Step 1: Login as auditor
    driver.get(f"{base_url}/login")
    WebDriverWait(driver, 10).until(
        EC.presence_of_element_located((By.ID, "username"))
    )
    driver.find_element(By.ID, "username").send_keys("auditor")
    driver.find_element(By.ID, "password").send_keys("Auditor123!")
    driver.find_element(By.ID, "submit").click()
    
    # Navigate to create audit
    driver.get(f"{base_url}/auditor/audits/new")
    WebDriverWait(driver, 10).until(
        EC.presence_of_element_located((By.ID, "audit-facility"))
    )
    
    # Create audit
    facility_select = Select(driver.find_element(By.ID, "audit-facility"))
    if len(facility_select.options) > 1:
        facility_select.select_by_index(1)
    driver.find_element(By.ID, "audit-date").send_keys(datetime.now().strftime("%Y-%m-%d"))
    driver.find_element(By.ID, "submit").click()
    
    # Wait for audit detail
    WebDriverWait(driver, 10).until(
        EC.presence_of_element_located((By.ID, "save-responses"))
    )
    
    # Step 2: Fill checklist with at least 1 FAIL
    result_selects = driver.find_elements(By.CSS_SELECTOR, "select[id^='result-']")
    if result_selects:
        first_select = Select(result_selects[0])
        first_select.select_by_value("FAIL")
        
        # Add comment
        comment_fields = driver.find_elements(By.CSS_SELECTOR, "textarea[id^='comment-']")
        if comment_fields:
            comment_fields[0].send_keys("Test failure comment")
        
        # Save responses
        driver.find_element(By.ID, "save-responses").click()
        WebDriverWait(driver, 10).until(
            EC.presence_of_element_located((By.XPATH, "//div[contains(text(), 'successfully')]"))
        )
    
    # Step 3: Create NC
    nc_links = driver.find_elements(By.XPATH, "//a[contains(text(), 'Create NC')]")
    if nc_links:
        nc_links[0].click()
        WebDriverWait(driver, 10).until(
            EC.presence_of_element_located((By.ID, "nc-severity"))
        )
        
        severity_select = Select(driver.find_element(By.ID, "nc-severity"))
        severity_select.select_by_value("HIGH")
        driver.find_element(By.ID, "nc-description").send_keys("Test non-conformity description")
        driver.find_element(By.ID, "submit").click()
        WebDriverWait(driver, 10).until(
            EC.presence_of_element_located((By.XPATH, "//h2[contains(text(), 'Non-Conformities')]"))
        )
    
    # Step 4: Logout and login as manager
    driver.get(f"{base_url}/logout")
    driver.get(f"{base_url}/login")
    WebDriverWait(driver, 10).until(
        EC.presence_of_element_located((By.ID, "username"))
    )
    driver.find_element(By.ID, "username").send_keys("manager")
    driver.find_element(By.ID, "password").send_keys("Manager123!")
    driver.find_element(By.ID, "submit").click()
    
    # Navigate to non-conformities
    driver.get(f"{base_url}/manager/nonconformities")
    WebDriverWait(driver, 10).until(
        EC.presence_of_element_located((By.XPATH, "//h1[contains(text(), 'Non-Conformities')]"))
    )
    
    # Click on first NC
    nc_links = driver.find_elements(By.XPATH, "//a[contains(@href, '/manager/nonconformities/') and contains(text(), 'View')]")
    if nc_links:
        nc_links[0].click()
        WebDriverWait(driver, 10).until(
            EC.presence_of_element_located((By.ID, "ca-owner-name"))
        )
        
        # Step 5: Create CA
        driver.find_element(By.ID, "ca-owner-name").send_keys("John Doe")
        driver.find_element(By.ID, "ca-due-date").send_keys((datetime.now() + timedelta(days=30)).strftime("%Y-%m-%d"))
        driver.find_element(By.ID, "ca-action-text").send_keys("Fix the non-conformity issue")
        driver.find_element(By.ID, "submit").click()
        WebDriverWait(driver, 10).until(
            EC.presence_of_element_located((By.XPATH, "//div[contains(text(), 'successfully')]"))
        )
        
        # Step 6: Mark CA as DONE
        done_buttons = driver.find_elements(By.XPATH, "//button[contains(text(), 'Mark as DONE')]")
        if done_buttons:
            done_buttons[0].click()
            WebDriverWait(driver, 10).until(
                EC.presence_of_element_located((By.XPATH, "//div[contains(text(), 'successfully')]"))
            )
        
        # Step 7: Close NC
        status_select = Select(driver.find_element(By.ID, "nc-status"))
        status_select.select_by_value("CLOSED")
        driver.find_element(By.ID, "update-status").click()
        WebDriverWait(driver, 10).until(
            EC.presence_of_element_located((By.XPATH, "//div[contains(text(), 'successfully')]"))
        )
        
        # Step 8: Verify NC is CLOSED
        status_select = Select(driver.find_element(By.ID, "nc-status"))
        assert status_select.first_selected_option.get_attribute("value") == "CLOSED"
