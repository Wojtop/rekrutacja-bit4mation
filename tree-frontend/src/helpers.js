import {useCallback, useState} from "react";
import {createNotification} from "./App";

export const useCenteredTree = (defaultTranslate = {x: 0, y: 0}) => {
    const [translate, setTranslate] = useState(defaultTranslate);
    const containerRef = useCallback((containerElem) => {
        if (containerElem !== null) {
            const {width, height} = containerElem.getBoundingClientRect();
            setTranslate({x: width / 2, y: height / 5});
        }
    }, []);
    return [translate, containerRef];
};

export function translateData(treeResponse) {
    console.log(treeResponse)
    // let references = Object.values(treeResponse.references);
    let references = new Map();
    treeResponse.references.forEach(element => {
        references.set(element.id, element.value)
    });
    // let references = new Map(Object.entries(treeResponse.references));
    let relations = new Map(Object.entries(treeResponse.relations));
    let leafs = new Map(Object.entries(treeResponse.leafs));
    let modified = treeResponse.modified
    console.log("references: ", references)
    console.log("relations: ", relations)
    console.log("leafs: ", leafs)
    console.log("Modified: ", modified)

    let visualTreeElems = new Map();
    references.forEach((value, key, map) => {
        // console.log("In map foreach. Key: ", key, " Value: ", value)
        visualTreeElems.set(key.toString(), {
            name: value.toString(),
            attributes: {"id": key.toString(), "parentId": null,},
            children: []
        })
    })
    console.log("Visual tree elems: ", visualTreeElems)
    let childrenIds = [];   // list of all childrens ID. ID which is not here is a root

    relations.forEach((relationChildren, parentId, map) => {
        childrenIds.push(...relationChildren)
        console.log("relation children: ", relationChildren)
        relationChildren.forEach(childId => {
            let parent = visualTreeElems.get(parentId.toString())
            // console.log("Parent Id: ", parentId," Parent: ",parent)
            parent.children.push(visualTreeElems.get(childId.toString()))
            visualTreeElems.get(childId.toString()).attributes.parentId = parentId;
        })
    })
    leafs.forEach((sum, lastNodeId, map) => {
            visualTreeElems.get(lastNodeId.toString()).children.push({name: sum.toString(), children: []})
        }
    )

    console.log("Visual tree: ", visualTreeElems)
    let possibleRoots = []
    visualTreeElems.forEach((v, key, map) => {
        if (!childrenIds.includes(parseInt(key))) {
            possibleRoots.push(key)
        }
    })
    console.log("Possible roots: ", possibleRoots)
    console.log("returned data: ", visualTreeElems.get(possibleRoots.toString()))
    return {tree: visualTreeElems.get(possibleRoots.toString()), modified: treeResponse.modified};
}

const serverAdress = 'localhost'
const serverPort = 8080

export const getData = async (setData, setLoading) => {
    callApi({method: 'GET'}, `http://${serverAdress}:${serverPort}/tree/getTree`, setData, setLoading)
}

export const callApi = async (postParams, url, setData, setLoading) => {
    fetch(url, postParams)
        .then((response) => {
            return response.json(); // Parse the response data as JSON
        })
        .then((actualData) => {
            let temp = translateData(actualData)
            actualData = temp.tree
            console.log("Tree data: ", actualData)
            setData(actualData);
        })
        .catch((err) => {
            createNotification('error', `${err.code}: ${err.message}`)
        })
        .finally(() => {
            setLoading(false);
            console.log("Loading set to false")
        });
}

export const callDeleteNode = async (nodeId, setData, setLoading) => {
    callApi({method: 'DELETE'}, `http://${serverAdress}:${serverPort}/tree/deleteNode/${nodeId}`, setData, setLoading)
        .then(()=>{createNotification("success", "Delete successful")})
}

export const callAddNode = (newNode, setResponse, setTreeData, close) => {
    if (newNode.parentId == null) {
        console.log("Sent node: ", newNode)
        fetch(`http://${serverAdress}:${serverPort}/tree/addNode`, {
            method: 'POST', headers: {
                "Content-Type": "application/json; charset=utf-8"
            }, body: JSON.stringify(newNode)
        })
            .then((res) => res.json())
            .then((json) => {setResponse(json)
                createNotification("success", `New root created`)
            })
            .catch((err) => {
                console.log("New root send error: ", err)
                createNotification("error", `${err.code}: ${err.message}`)
            })
    } else {
        fetch(`http://${serverAdress}:${serverPort}/tree/addNode`, {
            method: 'POST', headers: {
                "Content-Type": "application/json; charset=utf-8"
            }, body: JSON.stringify(newNode)
        })
            .then((res) => res.json())
            .then((json) => {
                    let res = translateData(json)
                    setTreeData(res.tree)
                    console.log("Updated tree data set: ", res)
                    close()
                    createNotification("success", "New node created")
                }
            )
            .catch((err) => {
                console.log("Send new node error: ", err)
                createNotification("error", `${err.code}: ${err.message}`)
            })
    }


    // callApi({method:'POST'},`http://${serverAdress}:${serverPort}/tree/addNode` ,setData, setError, setLoading)
}

export const setNewRoot = (currentTree, newRoot, setTreeData, setLoading) => {
    if (currentTree === undefined){
        getData( setTreeData, setLoading)
        return
    }
    const oldRootModification = {
        nodeId: currentTree.attributes.id,
        value: parseInt(currentTree.name),
        parentId: newRoot.id
    }
    console.log("Editing old root: ", oldRootModification)
    callEditNode(oldRootModification, setTreeData)

}

export const callEditNode = (editedNode, setTreeData) => {
    fetch(`http://${serverAdress}:${serverPort}/tree/editNode`, {
        method: 'PUT', headers: {
            "Content-Type": "application/json; charset=utf-8"
        }, body: JSON.stringify(editedNode)
    })
        .then((res) => res.json())
        .then((json) => {
                let res = translateData(json)
                setTreeData(res.tree)
                console.log("Updated tree data set: ", res)
            createNotification("success", "Node changed")
            }
        )
        .catch((err) => {
            console.log("Send node error: ", err)
            createNotification("error", `${err.code}: ${err.message}`)
        })
}