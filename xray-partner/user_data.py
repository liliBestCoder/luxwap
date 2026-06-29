from dataclasses import dataclass
from typing import Optional

@dataclass
class UserData:
    user_id: Optional[str] = None
    email: Optional[str] = None
    flow: Optional[str] = None
    encryption: Optional[str] = None
    op: Optional[str] = None
