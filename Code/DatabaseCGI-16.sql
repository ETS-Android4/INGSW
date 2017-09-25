create sequence giveProtocol
minvalue 1000
increment by 1
start with 1000;

/

create sequence giveIdPaymentOrder
minvalue 1
increment by 1
start with 1;

/

create sequence giveIdBill
minvalue 1
increment by 1
start with 1;

/

create sequence giveIdCustomer
minvalue 1
increment by 1
start with 1;

/

create table Customer
(
idCustomer integer primary key,
fiscalNumber varchar2(16) unique not null,
name varchar2(50) not null,
surname varchar2(50) not null
);

/

create table backOfficeOperator(
  username varchar2(100),
  pass varchar2(100)
)  

/

create table Bill
(
idBill integer primary key,
customer integer not null,
trimester integer not null,
year integer not null,
amount number(5,2) not null,
status varchar2(100) DEFAULT 'UNPAID',
constraint bstatus check(status in ('PAID','UNPAID','PENDING')),
constraint ctrimester check (trimester IN(1,2,3,4)),
constraint fkBill foreign key (customer) references Customer(idCustomer) on delete cascade,
constraint u1 unique(customer,trimester,year)
);

/

create table PaymentOrder
(
idPaymentOrder integer primary key,
protocol integer,
status varchar2(100) not null ,
bill integer not null unique,
amount number(5,2),
constraint cstatus check ( status IN ('NOTIFIED','NOT ISSUED','ISSUED','SUSPENDED','PAID','NOT PERTINENT')),
constraint fkPO foreign key(bill) references Bill(idBill) on delete cascade,
constraint cprotocol check ((status <> 'NOT ISSUED' OR protocol IS NULL) AND (protocol IS NOT NULL OR status = 'NOT ISSUED'))
);


/


create or replace trigger InsertingPaymentOrder
before insert on PaymentOrder
for each row
declare
  billMonth integer;
  billYear integer;
  months integer;

begin
  
  :NEW.idPaymentOrder := giveIdPaymentOrder.nextval;
  
  SELECT b.TRIMESTER * 3 into billMonth FROM BILL b WHERE b.IDBILL = :NEW.BILL;
  SELECT YEAR into billYear FROM BILL WHERE IDBILL = :NEW.BILL;
  months := MONTHS_BETWEEN( to_date(current_date,'DD-MM-YY'),to_date('01-'||billMonth||'-'||billYear, 'DD-MM-YY')) - 1;--Ingiunzione scatta dopo 1 mese( -3 scatta dopo 3 mesi)
  
  if(months > 0) then
    select  months*(amount* 0.2 ) + 5 + amount into :NEW.amount from bill where idBill = :NEW.bill;
  else
    raise_application_error(-25452,'You cannot create a payment order because it is too soon ');
  end if;
end;

/

create or replace trigger afterInsertPaymentOrder
after insert on PaymentOrder
for each row

begin
  update bill
  set status = 'PENDING' 
  where idbill = :NEW.BILL;

end;  

/

create or replace trigger delPaymentOrder
after delete on paymentOrder
for each row

begin
  update bill
  set status = 'UNPAID'
  where idBill = :OLD.bill;
end;

/

create or replace trigger incIdBill
before insert on Bill
for each row
begin
  
  :NEW.idBill := giveIdBill.nextval;

end;  

/

create or replace TRIGGER beforeUpdatePaymentOrder
before update on PaymentOrder
for each row

begin
    if( :NEW.status = 'ISSUED' AND :OLD.status = 'NOT ISSUED') then
        :NEW.protocol := giveProtocol.nextval;
    end if;
end;

/

create or replace trigger afterUpdatePaymentOrder
after update on paymentOrder
for each row
begin
IF (:NEW.status = 'PAID') THEN
    UPDATE BILL
    SET STATUS = 'PAID'
    WHERE IDBILL = :NEW.BILL;
ElSIF (:NEW.status = 'NOT PERTINENT') THEN
    DELETE FROM BILL WHERE IDBILL = :NEW.BILL;
END IF;    
END;

/

create or replace trigger SetPaidBill
before update on PaymentOrder
for each row

begin

    if(:NEW.status = 'PAID') then
        UPDATE Bill
        SET status = 'PAID'
        WHERE idbill = :NEW.bill;
    end if;
    
end;

/

create or replace trigger incIdCustomer
before insert on Customer
for each row
begin
  
  :NEW.idCustomer := giveIdCustomer.nextval;

end;  
/

insert into backofficeoperator values('operator1','pass1');
insert into backofficeoperator values('operator2', 'pass2'); 

insert into Customer(FiscalNumber,Name,Surname) values ('LFNFNC70B14F839G','Francesco','Alfano');
insert into Customer(FiscalNumber,Name,Surname) values ('PCTMHL75L20F839A','Michele','Pacato');
insert into Customer(FiscalNumber,Name,Surname) values ('GPPDMR82A05F839S','Giuseppe','Di Maro');
insert into Customer(FiscalNumber,Name,Surname) values ('GCMCRN68B02F839A','Giacomo','Carino');

insert into Bill(Customer,trimester,year,amount,status) values (1,2,2016,90.50,'PAID');
insert into Bill(Customer,trimester,year,amount,status) values (1,3,2016,84.10,'PAID');
insert into Bill(Customer,trimester,year,amount,status) values (1,4,2016,150.40,'PAID');
insert into Bill(Customer,trimester,year,amount,status) values (1,1,2017,80,'PAID');
insert into Bill(Customer,trimester,year,amount,status) values (1,2,2017,50.10,'PAID');
insert into Bill(Customer,trimester,year,amount,status) values (2,1,2016,64,'PAID');
insert into Bill(Customer,trimester,year,amount,status) values (2,2,2016,50.20,'PAID');
insert into Bill(Customer,trimester,year,amount,status) values (2,3,2016,81.60,'PAID');
insert into Bill(Customer,trimester,year,amount) values (2,4,2016,50);
insert into Bill(Customer,trimester,year,amount) values (2,1,2017,40);
insert into Bill(Customer,trimester,year,amount) values (2,2,2017,60);
insert into Bill(Customer,trimester,year,amount) values (3,1,2016,84);
insert into Bill(Customer,trimester,year,amount) values (3,2,2016,45.80);
insert into Bill(Customer,trimester,year,amount) values (3,3,2016,70.10);
insert into Bill(Customer,trimester,year,amount) values (3,4,2016,75);
insert into Bill(Customer,trimester,year,amount) values (3,1,2017,90);
insert into Bill(Customer,trimester,year,amount) values (3,2,2017,82.40);
insert into Bill(Customer,trimester,year,amount) values (4,1,2016,91.50);
insert into Bill(Customer,trimester,year,amount) values (4,2,2016,90);
insert into Bill(Customer,trimester,year,amount) values (4,3,2016,95);
insert into Bill(Customer,trimester,year,amount) values (4,4,2016,85.40);
insert into Bill(Customer,trimester,year,amount) values (4,1,2017,98.10);
insert into Bill(Customer,trimester,year,amount) values (4,2,2017,100.20);

insert into paymentOrder (protocol,status,bill) values (5,'NOTIFIED',22); 
