import os
import pytest
from playwright.sync_api import sync_playwright

DEFAULT_BASE_URL = os.environ.get("TARGET_URL", "http://localhost:8000")
CHROME_PATH = r"C:\Program Files\Google\Chrome\Application\chrome.exe"

@pytest.fixture(scope="session")
def base_url():
    return DEFAULT_BASE_URL.rstrip("/")

@pytest.fixture(scope="session")
def browser_instance():
    with sync_playwright() as p:
        launch_args = {
            "headless": True,
            "args": [
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--disable-gpu",
                "--remote-debugging-port=9222"
            ]
        }
        # If installed system Chrome exists, use it
        if os.path.exists(CHROME_PATH):
            launch_args["executable_path"] = CHROME_PATH
            
        browser = p.chromium.launch(**launch_args)
        yield browser
        browser.close()

@pytest.fixture(scope="function")
def context(browser_instance):
    ctx = browser_instance.new_context(
        viewport={"width": 1440, "height": 900},
        ignore_https_errors=True
    )
    yield ctx
    ctx.close()

@pytest.fixture(scope="function")
def page(context):
    p = context.new_page()
    yield p
    p.close()

def login_as(page, base_url, username, password="123456"):
    """通用的多角色登录辅助函数"""
    page.goto(f"{base_url}/login")
    page.wait_for_selector("#username", timeout=10000)
    page.fill("#username", username)
    page.fill("#password", password)
    page.click("#loginBtn")
    page.wait_for_url(f"{base_url}/index", timeout=10000)
    page.wait_for_selector(".sidebar", timeout=10000)
