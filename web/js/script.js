function filter_resume() {
    let form = document.getElementById('search-form__form');
    form.submit();
}

function saveResume(uuid) {
    let form = document.getElementById("edit_form");
    let inp = form.querySelector("input[name='edit_uuid']");
    inp.removeAttribute("disabled");
    if (uuid) inp.value = uuid;
    form.submit();
}

function setInputWidth(inputElement) {
    let MAX_WIDTH = 320,
        MAX_COLS = 50,
        canvas = document.createElement("canvas"),
        context = canvas.getContext("2d");
    context.font = window.getComputedStyle(inputElement).font;
    let textLen = inputElement.value.length,
        rows = Math.ceil((textLen + 1) / MAX_COLS),
        name = inputElement.getAttribute("name"),
        width = context.measureText(inputElement.value).width;
    if (width > MAX_WIDTH) {
        if (inputElement.tagName.toLowerCase() === "input") {
            let newNode = document.createElement('textarea');
            newNode.classList = inputElement.classList;
            // newNode.className = 'resume-edit__input';
            newNode.value = inputElement.value;
            newNode.setAttribute("cols", `${MAX_COLS}`);
            newNode.setAttribute("name", name);
            newNode.style.resize = "none";
            newNode.addEventListener('input', event => setInputWidth(event.target));
            inputElement.parentNode.insertBefore(newNode, inputElement);
            inputElement.remove();
            inputElement = newNode;
            inputElement.focus();
        }
    } else {
        inputElement.style.width = Math.min(Math.max(100, Math.ceil(width) + 25), MAX_WIDTH) + 'px';
    }
    if (inputElement.tagName.toLowerCase() === "textarea") {
        inputElement.setAttribute("rows", `${rows}`);
    }
}

setListenersAndInputWidth(document);
let firstResumeEditInput = document.querySelector('.resume-edit__input');
if (firstResumeEditInput != null) firstResumeEditInput.focus();

function setListenersAndInputWidth(elem) {
    elem.querySelectorAll('.resume-edit__add-icon')
        .forEach(item => item
            .addEventListener('click',
                event => addElement(event.target))
        );
    elem.querySelectorAll('.resume-edit__delete-icon')
        .forEach(item => item
            .addEventListener('click',
                event => deleteElement(event.target))
        );
    elem.querySelectorAll('input')
        .forEach(item => {
            setInputWidth(item);
            item.addEventListener('input',
                event => setInputWidth(event.target));
        });
}

function deleteElement(elem) {
    let parentLi = elem.closest('li');
    let parentUl = parentLi.closest(".resume-edit__org-list");
    if (parentLi) parentLi.remove();
    if (parentUl) reindexPosition(parentUl);
}

function addElement(target) {
    const closestSectionItem = target.closest(".resume-edit__sections-item");
    const sectionTypeName = closestSectionItem
        .querySelector(".resume-edit__section_title")
        .dataset.sectionType;
    const currentOrgList = closestSectionItem.querySelector(".resume-edit__org-list");
    const closestOrg = target.closest(".resume-edit__org");

    if (closestOrg) { // adding position
        const allOrgs = currentOrgList.querySelectorAll(".resume-edit__org");
        insertElementAndSetListeners(closestOrg.querySelector(".resume-edit__positions-list"),
            "li", "resume-edit__position",
            getNewPositionHtml(sectionTypeName,
                Array.from(allOrgs).findIndex(item => item.isSameNode(closestOrg))));
    } else {
        if (currentOrgList) { //adding organization
            insertElementAndSetListeners(currentOrgList, "li",
                "resume-edit__org", getNewOrganizationHtml(sectionTypeName));
            reindexPosition(currentOrgList);
        } else { //adding text
            insertElementAndSetListeners(closestSectionItem
                    .querySelector(".resume-edit__text-list"),
                "li", "resume-edit__text",
                getNewTextHtml(sectionTypeName));
        }
    }
}

function reindexPosition(orgList) {
    orgList.querySelectorAll('.resume-edit__org')
        .forEach((orgLI, orgIndex) => {
            orgLI.querySelectorAll('input')
                .forEach(item => {
                    let itemNameAttr = item.getAttribute('name');
                    if (itemNameAttr.search('^\\w+_\\d+_\\w+$') >= 0) {
                        let nameArr = itemNameAttr.split('_');
                        nameArr[1] = String(orgIndex);
                        item.setAttribute('name', nameArr.join('_'));
                    }
                });
        });
}

function insertElementAndSetListeners(parent, itemTag, itemClass, itemHtml, last) {
    let newElem = document.createElement(itemTag);
    newElem.classList.add(itemClass);
    newElem.insertAdjacentHTML("afterbegin", itemHtml);
    parent.insertBefore(newElem, last ? parent.lastElementChild : parent.firstChild);
    setListenersAndInputWidth(newElem);
    newElem.querySelectorAll("select[name='search-type']")
        .forEach(item => item.addEventListener("change",
            e => selectChanged(e)))
    return newElem;
}

function filter_click() {
    // search-form backbone insertion
    const newSearchForm = insertElementAndSetListeners(
        document.querySelector('.container'),
        "div", "search-form", getSearchFormHtml());
    const newSearchFormItem = insertElementAndSetListeners(
        newSearchForm.querySelector(".search-form__fieldset"),
        "div", "search-form__item",
        getSearchFormItemHtml(0));

    const typeInputElem = newSearchFormItem.querySelector("select[name='search-type']");
    fillSelectTypeForFilter(typeInputElem);
    fillValuesForSelectedType(typeInputElem.value, 0);
    newSearchForm.querySelectorAll(".resume-button")
        .forEach(b => b.addEventListener('click',
            e => searchFormButtonClick(e)));
    // hide filter-link
    document.body.querySelector('.filter-link').classList.add('disabled');
}

function fillSelectTypeForFilter(selectElem, exclude) {
    selectElem.querySelectorAll("option")
        .forEach(e => e.remove());
    Object.keys(searchMapJson).forEach(item => {
        if (!exclude || !exclude.includes(item)) {
            let opt = document.createElement('option');
            opt.value = item;
            opt.innerHTML = item;
            selectElem.appendChild(opt);
        }
    });
}

function fillValuesForSelectedType(selectedType, n) {
    let datalist = document.getElementById(`search-form__content-list-${n}`);
    datalist.querySelectorAll("option").forEach(item => item.remove())
    for (const s of searchMapJson[selectedType].sort()) {
        let opt = document.createElement('option');
        opt.value = s;
        opt.innerHTML = s;
        datalist.appendChild(opt);
    }
}

function searchFormButtonClick(e) {
    switch (e.target.id) {
        case "search-form__add-filter-button":
            e.preventDefault();
            addFilterItem();
            break;
        case "search-form__submit-button":
            // e.preventDefault();
            document.querySelector(".search-form__form").submit();
    }
}

function selectChanged(e) {
    const dataListIndex = e.target.closest(".search-form__item")
        .querySelector("[name='search-content']")
        .getAttribute("list").split("-").slice(-1).pop();
    fillValuesForSelectedType(e.target.value, dataListIndex);
}

function addFilterItem() {
    const existingSelectElements = document.querySelectorAll("select[name='search-type']");
    const filterIndex = existingSelectElements.length;
    const createdItem = insertElementAndSetListeners(
        document.querySelector('.search-form__fieldset'),
        "div", "search-form__item",
        getSearchFormItemHtml(existingSelectElements.length), document.querySelector(".search-form__buttons"));
    const typeInputElem = createdItem.querySelector("select[name='search-type']");
    fillSelectTypeForFilter(typeInputElem
            , Array.from(existingSelectElements).map(item => item.value)
    );
    fillValuesForSelectedType(typeInputElem.value, filterIndex);
    existingSelectElements.forEach(elem => {
        elem.querySelectorAll("option").forEach(o => {
            if (o.value !== elem.value) o.remove();
        })
    })
}

function getSearchFormHtml() {
    return `<form class="search-form__form" method="get">
    <fieldset class="search-form__fieldset">
        <legend>Filter</legend>
        <div class="search-form__buttons">
            <a class="resume-button" id="search-form__add-filter-button">Add filter</a>
            <a class="resume-button" id="search-form__submit-button">Apply</a>
            <a href="${pageContext}" class="resume-button">Cancel</a>
        </div>
    </fieldset>
</form>`;
}

function getSearchFormItemHtml(n) {
    return `<label>by:
    <select name="search-type" class="resume-search__input" required> </select>
</label>
<label> = <input autocomplete="off" name="search-content" class="resume-search__input" list="search-form__content-list-${n}">
</label>
<datalist id="search-form__content-list-${n}"></datalist>`
}

function getNewPositionHtml(sectionType, orgIndex) {
    return `<span class="resume-edit__delete-icon"> <img src="${pageContext}/img/delete.svg" alt=""> </span>
<label>From: <input 
    type="text" class="resume-edit__input" name="${sectionType}_${orgIndex}_posstart" value="01/2000"> </label>
<label>to: <input type="text" class="resume-edit__input" name="${sectionType}_${orgIndex}_posend" value="01/2000"> </label>
<label>Title: <input type="text" class="resume-edit__input" name="${sectionType}_${orgIndex}_postitle" value="empty"> </label>
<label>Description: <input type="text" class="resume-edit__input" name="${sectionType}_${orgIndex}_posdescr" value="empty"> </label>`;
}

function getNewOrganizationHtml(sectionType) {
    return `<span class="resume-edit__add-icon">
    <img src="${pageContext}/img/add.svg" alt="">
</span>
<span class="resume-edit__delete-icon">
    <img src="${pageContext}/img/delete.svg" alt="">
</span>
<label class="resume-edit__org-name">Organization: <input 
    type="text" class="resume-edit__input"
          autocomplete="off" name="${sectionType}" value="empty">
</label>
<label class="resume-edit__org-link">URL: <input 
    type="text" class="resume-edit__input"
          autocomplete="off" name="${sectionType}_url"
           value="">
</label>
<ul class="resume-edit__positions-list"></ul>`;
}

function getNewTextHtml(sectionType) {
    return `<span class="resume-edit__delete-icon">
    <img src="${pageContext}/img/delete.svg" alt="">
</span><label><input type="text" autocomplete="off" class="resume-edit__input"
         name="${sectionType.toString()}" value=""></label>`;
}