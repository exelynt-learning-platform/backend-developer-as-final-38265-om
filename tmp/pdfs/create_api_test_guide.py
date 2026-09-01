from reportlab.lib import colors
from reportlab.lib.enums import TA_CENTER
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.units import cm
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle, PageBreak, KeepTogether
from reportlab.pdfbase.pdfmetrics import stringWidth
from reportlab.lib.colors import HexColor

OUT = r"C:\Users\HP\Documents\Booking\Booking\output\pdf\resource-booking-postman-test-guide.pdf"

styles = getSampleStyleSheet()
styles.add(ParagraphStyle(name="TitleGuide", parent=styles["Title"], fontName="Helvetica-Bold", fontSize=22,
                          leading=27, textColor=HexColor("#17365D"), alignment=TA_CENTER, spaceAfter=9))
styles.add(ParagraphStyle(name="Subtitle", parent=styles["Normal"], fontSize=10, leading=14,
                          textColor=HexColor("#4F5965"), alignment=TA_CENTER, spaceAfter=18))
styles.add(ParagraphStyle(name="H1Guide", parent=styles["Heading1"], fontName="Helvetica-Bold", fontSize=15,
                          leading=19, textColor=HexColor("#17365D"), spaceBefore=12, spaceAfter=7))
styles.add(ParagraphStyle(name="H2Guide", parent=styles["Heading2"], fontName="Helvetica-Bold", fontSize=11.5,
                          leading=14, textColor=HexColor("#1F4E79"), spaceBefore=9, spaceAfter=4))
styles.add(ParagraphStyle(name="BodyGuide", parent=styles["BodyText"], fontSize=9, leading=12, spaceAfter=5))
styles.add(ParagraphStyle(name="CodeGuide", parent=styles["Code"], fontName="Courier", fontSize=7.3,
                          leading=9.4, leftIndent=6, rightIndent=6, borderColor=HexColor("#D9E2F3"),
                          borderWidth=0.5, borderPadding=6, backColor=HexColor("#F7F9FC"), spaceBefore=3, spaceAfter=7))
styles.add(ParagraphStyle(name="NoteGuide", parent=styles["BodyText"], fontSize=8.5, leading=11,
                          leftIndent=8, borderColor=HexColor("#9DC3E6"), borderWidth=0, borderPadding=6,
                          backColor=HexColor("#EAF3FA"), spaceAfter=7))

def p(text, style="BodyGuide"):
    return Paragraph(text, styles[style])

def code(text):
    return Paragraph(text.replace("\n", "<br/>"), styles["CodeGuide"])

def endpoint(method, url, access, purpose, body=None, expected=None):
    method_color = {"GET": "2E7D32", "POST": "1565C0", "PUT": "EF6C00", "DELETE": "C62828"}[method]
    data = [[p(f'<font color="#{method_color}"><b>{method}</b></font>', "BodyGuide"),
             p(f"<b>{url}</b>", "BodyGuide"), p(access, "BodyGuide")]]
    table = Table(data, colWidths=[1.7*cm, 8.8*cm, 5.2*cm])
    table.setStyle(TableStyle([("BACKGROUND", (0,0), (-1,-1), HexColor("#F5F8FC")),
                               ("BOX", (0,0), (-1,-1), 0.4, HexColor("#D4DDE8")),
                               ("VALIGN", (0,0), (-1,-1), "TOP"),
                               ("LEFTPADDING", (0,0), (-1,-1), 6), ("RIGHTPADDING", (0,0), (-1,-1), 6),
                               ("TOPPADDING", (0,0), (-1,-1), 5), ("BOTTOMPADDING", (0,0), (-1,-1), 4)]))
    items = [table, p(purpose)]
    if body:
        items.extend([p("Request body:", "H2Guide"), code(body)])
    if expected:
        items.append(p(f"<b>Expected:</b> {expected}"))
    items.append(Spacer(1, 6))
    return items

def header_footer(canvas, doc):
    canvas.saveState()
    canvas.setStrokeColor(HexColor("#D4DDE8")); canvas.line(1.5*cm, 1.35*cm, A4[0]-1.5*cm, 1.35*cm)
    canvas.setFont("Helvetica", 8); canvas.setFillColor(HexColor("#667085"))
    canvas.drawString(1.5*cm, 0.85*cm, "Resource Booking API - Postman Test Guide")
    canvas.drawRightString(A4[0]-1.5*cm, 0.85*cm, f"Page {doc.page}")
    canvas.restoreState()

story = []
story += [p("Resource Booking API", "TitleGuide"), p("Complete Postman endpoint testing guide", "Subtitle")]
story += [p("Before you test", "H1Guide")]
story += [p("Run the application from <b>C:\\Users\\HP\\Documents\\Booking\\Booking</b> using <b>mvn spring-boot:run</b>. The default server address is <b>http://localhost:8080</b>. MySQL must be running and configured through DB_URL, DB_USERNAME, and DB_PASSWORD if the defaults do not match your system.")]
story += [p("Create a Postman environment", "H2Guide")]
env = [[p("Variable", "BodyGuide"), p("Initial value", "BodyGuide")],
       [p("baseUrl", "BodyGuide"), p("http://localhost:8080", "BodyGuide")],
       [p("adminToken", "BodyGuide"), p("Leave blank; fill after admin login", "BodyGuide")],
       [p("userToken", "BodyGuide"), p("Leave blank; fill after user login", "BodyGuide")],
       [p("resourceId", "BodyGuide"), p("Leave blank; fill after resource creation", "BodyGuide")],
       [p("reservationId", "BodyGuide"), p("Leave blank; fill after reservation creation", "BodyGuide")]]
t = Table(env, colWidths=[4.3*cm, 11.4*cm])
t.setStyle(TableStyle([("BACKGROUND", (0,0), (-1,0), HexColor("#17365D")), ("TEXTCOLOR", (0,0), (-1,0), colors.white),
                        ("GRID", (0,0), (-1,-1), 0.35, HexColor("#D4DDE8")), ("VALIGN", (0,0), (-1,-1), "TOP"),
                        ("ROWBACKGROUNDS", (0,1), (-1,-1), [colors.white, HexColor("#F8FAFC")]), ("PADDING", (0,0), (-1,-1), 6)]))
story += [t, Spacer(1, 10), p("For every JSON request use Header: <b>Content-Type: application/json</b>. For protected endpoints use Authorization type <b>Bearer Token</b> and paste the appropriate token.", "NoteGuide")]

story += [p("1. Authentication", "H1Guide")]
story += endpoint("POST", "{{baseUrl}}/auth/login", "Public", "Log in and receive a JWT. Do not escape the @ character in the password.",
                  '{<br/>  "username": "admin",<br/>  "password": "Admin@123"<br/>}',
                  "200 OK. Copy the token value to adminToken.")
story += endpoint("POST", "{{baseUrl}}/auth/login", "Public", "Log in as a normal user.",
                  '{<br/>  "username": "user",<br/>  "password": "User@123"<br/>}',
                  "200 OK. Copy the token value to userToken.")
story += [p("Seed users", "H2Guide"), p("ADMIN: admin / Admin@123. USER: user / User@123. If an older database already has these usernames, the initializer intentionally does not overwrite their password or role. Confirm the users table stores roles ADMIN and USER.")]

story += [PageBreak(), p("2. Resource endpoints", "H1Guide"), p("First log in as ADMIN and set Authorization to Bearer {{adminToken}} for create, update, and delete.")]
resource_body = '{<br/>  "name": "Conference Room A",<br/>  "description": "Room with projector and 12 seats",<br/>  "available": true,<br/>  "price": 500.00<br/>}'
story += endpoint("POST", "{{baseUrl}}/resources", "ADMIN", "Create a bookable resource. Save the response id as resourceId.", resource_body, "201 Created.")
story += endpoint("GET", "{{baseUrl}}/resources", "ADMIN or USER", "Retrieve every resource.", expected="200 OK.")
story += endpoint("GET", "{{baseUrl}}/resources/{{resourceId}}", "ADMIN or USER", "Retrieve one resource by Long ID.", expected="200 OK; 404 if the ID does not exist.")
story += endpoint("PUT", "{{baseUrl}}/resources/{{resourceId}}", "ADMIN", "Replace the resource values.",
                  '{<br/>  "name": "Conference Room A",<br/>  "description": "Updated room description",<br/>  "available": true,<br/>  "price": 650.00<br/>}', "200 OK.")
story += endpoint("DELETE", "{{baseUrl}}/resources/{{resourceId}}", "ADMIN", "Delete the resource after completing reservation tests.", expected="204 No Content.")

story += [PageBreak(), p("3. Reservation endpoints", "H1Guide"), p("Create a resource first. Use a resource with available set to true. USER and ADMIN can create a reservation; the authenticated JWT always determines the reservation owner.")]
reservation_body = '{<br/>  "resourceId": {{resourceId}},<br/>  "startTime": "2026-09-10T10:00:00",<br/>  "endTime": "2026-09-10T12:00:00"<br/>}'
story += endpoint("POST", "{{baseUrl}}/reservations", "ADMIN or USER", "Create a reservation. Set Bearer {{userToken}} to test USER behavior. Do not send userId; it is not accepted by the request DTO.", reservation_body, "201 Created. Save response id as reservationId. Status is PENDING and price comes from the resource.")
story += endpoint("GET", "{{baseUrl}}/reservations", "ADMIN or USER", "List reservations. ADMIN receives all; USER receives only owned reservations.", expected="200 OK with a paginated Page response.")
story += endpoint("GET", "{{baseUrl}}/reservations/{{reservationId}}", "ADMIN or owning USER", "Get a single reservation. A USER attempting to read another user's reservation receives 403.", expected="200 OK, 403 Forbidden, or 404 Not Found.")
story += endpoint("PUT", "{{baseUrl}}/reservations/{{reservationId}}", "ADMIN", "Update resource/times and optionally status.",
                  '{<br/>  "resourceId": {{resourceId}},<br/>  "startTime": "2026-09-10T10:00:00",<br/>  "endTime": "2026-09-10T12:00:00",<br/>  "status": "CONFIRMED"<br/>}', "200 OK. Status may be PENDING, CONFIRMED, or CANCELLED.")
story += endpoint("DELETE", "{{baseUrl}}/reservations/{{reservationId}}", "ADMIN", "Delete the reservation.", expected="204 No Content.")

story += [PageBreak(), p("4. Reservation filter, pagination, and sorting", "H1Guide")]
filter_rows = [[p("Test URL", "BodyGuide"), p("Purpose", "BodyGuide")],
               [p("{{baseUrl}}/reservations?status=PENDING", "BodyGuide"), p("Only PENDING reservations.", "BodyGuide")],
               [p("{{baseUrl}}/reservations?minPrice=100&amp;maxPrice=1000", "BodyGuide"), p("Reservations within the inclusive price range.", "BodyGuide")],
               [p("{{baseUrl}}/reservations?page=0&amp;size=10", "BodyGuide"), p("First page containing at most ten results.", "BodyGuide")],
               [p("{{baseUrl}}/reservations?sortBy=price&amp;sortDirection=desc", "BodyGuide"), p("Highest price first.", "BodyGuide")],
               [p("{{baseUrl}}/reservations?status=PENDING&amp;minPrice=100&amp;maxPrice=1000&amp;page=0&amp;size=10&amp;sortBy=startTime&amp;sortDirection=asc", "BodyGuide"), p("Full required filtering, pagination, and sorting example.", "BodyGuide")]]
ft = Table(filter_rows, colWidths=[10.8*cm, 4.9*cm])
ft.setStyle(TableStyle([("BACKGROUND", (0,0), (-1,0), HexColor("#17365D")), ("TEXTCOLOR", (0,0), (-1,0), colors.white),
                         ("GRID", (0,0), (-1,-1), 0.35, HexColor("#D4DDE8")), ("VALIGN", (0,0), (-1,-1), "TOP"),
                         ("ROWBACKGROUNDS", (0,1), (-1,-1), [colors.white, HexColor("#F8FAFC")]), ("PADDING", (0,0), (-1,-1), 6)]))
story += [ft, Spacer(1, 10), p("Use Bearer {{adminToken}} to confirm all matching reservations are returned. Use Bearer {{userToken}} to confirm the exact same filters are restricted to that user's reservations.", "NoteGuide")]

story += [p("5. Required security and validation checks", "H1Guide")]
checks = [[p("Test", "BodyGuide"), p("How", "BodyGuide"), p("Expected result", "BodyGuide")],
          [p("No token", "BodyGuide"), p("GET /resources without Authorization", "BodyGuide"), p("401 Unauthorized", "BodyGuide")],
          [p("Bad token", "BodyGuide"), p("GET /resources with Bearer invalid-token", "BodyGuide"), p("401 Unauthorized", "BodyGuide")],
          [p("USER resource mutation", "BodyGuide"), p("POST /resources using userToken", "BodyGuide"), p("403 Forbidden", "BodyGuide")],
          [p("USER reservation mutation", "BodyGuide"), p("PUT or DELETE /reservations/{id} using userToken", "BodyGuide"), p("403 Forbidden", "BodyGuide")],
          [p("Ownership", "BodyGuide"), p("Create a reservation as one USER; request it using another USER token", "BodyGuide"), p("403 Forbidden", "BodyGuide")],
          [p("Missing resource fields", "BodyGuide"), p('POST /resources with name "" or missing price', "BodyGuide"), p("400 Bad Request", "BodyGuide")],
          [p("Negative price", "BodyGuide"), p("POST /resources with price -10", "BodyGuide"), p("400 Bad Request", "BodyGuide")],
          [p("Invalid time range", "BodyGuide"), p("POST /reservations where endTime is before startTime", "BodyGuide"), p("400 Bad Request", "BodyGuide")],
          [p("Invalid status", "BodyGuide"), p('PUT /reservations/{id} with status "INVALID"', "BodyGuide"), p("400 Bad Request", "BodyGuide")]]
ct = Table(checks, colWidths=[4.1*cm, 8.1*cm, 3.5*cm])
ct.setStyle(TableStyle([("BACKGROUND", (0,0), (-1,0), HexColor("#17365D")), ("TEXTCOLOR", (0,0), (-1,0), colors.white),
                         ("GRID", (0,0), (-1,-1), 0.35, HexColor("#D4DDE8")), ("VALIGN", (0,0), (-1,-1), "TOP"),
                         ("ROWBACKGROUNDS", (0,1), (-1,-1), [colors.white, HexColor("#F8FAFC")]), ("PADDING", (0,0), (-1,-1), 5)]))
story += [ct]

story += [PageBreak(), p("Suggested Postman execution order", "H1Guide")]
steps = [
    "1. Start MySQL and the Spring Boot application.",
    "2. Log in as ADMIN; save the returned token as adminToken.",
    "3. Create a resource as ADMIN; save its id as resourceId.",
    "4. Log in as USER; save the returned token as userToken.",
    "5. Read resources as USER.",
    "6. Create a reservation as USER; save its id as reservationId.",
    "7. List/filter reservations as USER and verify only that user's results are returned.",
    "8. Retrieve the reservation by ID as USER.",
    "9. Use adminToken to list all reservations and update the USER reservation status.",
    "10. Execute the permission and validation checks on the previous page.",
    "11. Delete the reservation and resource as ADMIN if cleanup is desired."
]
for step in steps:
    story += [p(step)]
story += [Spacer(1, 8), p("Swagger UI", "H2Guide"), p("When the application is running, interactive OpenAPI documentation is available at http://localhost:8080/swagger-ui/index.html.")]

doc = SimpleDocTemplate(OUT, pagesize=A4, rightMargin=1.5*cm, leftMargin=1.5*cm, topMargin=1.4*cm, bottomMargin=1.7*cm,
                        title="Resource Booking API - Postman Test Guide", author="Codex")
doc.build(story, onFirstPage=header_footer, onLaterPages=header_footer)
